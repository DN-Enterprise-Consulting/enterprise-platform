package de.dn.enterprise.platform.knowledge.persistence;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectMetadata;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectStatus;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectVersion;
import de.dn.enterprise.platform.knowledge.spi.KnowledgePersistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class FileKnowledgePersistence implements KnowledgePersistence {

    private static final String FILE_EXTENSION = ".kobj";
    private final Path rootDirectory;

    public FileKnowledgePersistence(Path rootDirectory) {
        this.rootDirectory = Objects.requireNonNull(rootDirectory, "rootDirectory must not be null");
    }

    @Override
    public KnowledgeObject save(KnowledgeObject knowledgeObject) {
        Objects.requireNonNull(knowledgeObject, "knowledgeObject must not be null");
        try {
            Files.createDirectories(rootDirectory);
            Files.writeString(fileFor(knowledgeObject.id()), serialize(knowledgeObject), StandardCharsets.UTF_8);
            return knowledgeObject;
        } catch (IOException exception) {
            throw new KnowledgePersistenceException("Could not save knowledge object " + knowledgeObject.id(), exception);
        }
    }

    @Override
    public Optional<KnowledgeObject> load(KnowledgeObjectId id) {
        Objects.requireNonNull(id, "id must not be null");
        Path file = fileFor(id);
        if (!Files.exists(file)) return Optional.empty();
        try {
            return Optional.of(deserialize(Files.readString(file, StandardCharsets.UTF_8)));
        } catch (IOException | RuntimeException exception) {
            throw new KnowledgePersistenceException("Could not load knowledge object " + id, exception);
        }
    }

    @Override
    public boolean exists(KnowledgeObjectId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Files.isRegularFile(fileFor(id));
    }

    @Override
    public void delete(KnowledgeObjectId id) {
        Objects.requireNonNull(id, "id must not be null");
        try {
            Files.deleteIfExists(fileFor(id));
        } catch (IOException exception) {
            throw new KnowledgePersistenceException("Could not delete knowledge object " + id, exception);
        }
    }

    public List<KnowledgeObject> loadAll() {
        if (!Files.exists(rootDirectory)) return List.of();
        try (var paths = Files.list(rootDirectory)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(FILE_EXTENSION))
                    .map(this::loadFile)
                    .toList();
        } catch (IOException exception) {
            throw new KnowledgePersistenceException("Could not list persisted knowledge objects", exception);
        }
    }

    private KnowledgeObject loadFile(Path path) {
        try {
            return deserialize(Files.readString(path, StandardCharsets.UTF_8));
        } catch (IOException | RuntimeException exception) {
            throw new KnowledgePersistenceException("Could not load persistence file " + path, exception);
        }
    }

    private Path fileFor(KnowledgeObjectId id) {
        return rootDirectory.resolve(id.value() + FILE_EXTENSION);
    }

    private static String serialize(KnowledgeObject object) {
        return String.join("\n",
                "id=" + object.id().value(),
                "type=" + object.type().name(),
                "version=" + object.version().major() + "." + object.version().minor() + "." + object.version().patch(),
                "status=" + object.status().name(),
                "name=" + encode(object.metadata().name()),
                "description=" + encode(object.metadata().description()),
                "attributes=" + encodeAttributes(object.metadata().attributes()),
                "content=" + encode(object.content()));
    }

    private static KnowledgeObject deserialize(String serialized) {
        Map<String, String> values = new HashMap<>();
        for (String line : serialized.split("\\R", -1)) {
            int separator = line.indexOf('=');
            if (separator <= 0) throw new IllegalArgumentException("Invalid persistence record");
            values.put(line.substring(0, separator), line.substring(separator + 1));
        }

        KnowledgeObjectId id = KnowledgeObjectId.of(java.util.UUID.fromString(required(values, "id")));
        KnowledgeObjectType type = KnowledgeObjectType.valueOf(required(values, "type"));
        String[] versionParts = required(values, "version").split("\\.", -1);
        if (versionParts.length != 3) throw new IllegalArgumentException("Invalid version");

        KnowledgeObjectVersion version = new KnowledgeObjectVersion(
                Integer.parseInt(versionParts[0]),
                Integer.parseInt(versionParts[1]),
                Integer.parseInt(versionParts[2]));

        KnowledgeObjectStatus status = KnowledgeObjectStatus.valueOf(required(values, "status"));
        KnowledgeObjectMetadata metadata = new KnowledgeObjectMetadata(
                decode(required(values, "name")),
                decode(required(values, "description")),
                decodeAttributes(required(values, "attributes")));

        return new KnowledgeObject(
                id, type, version, status, metadata, decode(required(values, "content")));
    }

    private static String required(Map<String, String> values, String key) {
        String value = values.get(key);
        if (value == null) throw new IllegalArgumentException("Missing field: " + key);
        return value;
    }

    private static String encode(String value) {
        return java.util.Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(java.util.Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private static String encodeAttributes(Map<String, String> attributes) {
        List<String> entries = new ArrayList<>();
        attributes.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> entries.add(encode(entry.getKey()) + ":" + encode(entry.getValue())));
        return String.join(",", entries);
    }

    private static Map<String, String> decodeAttributes(String value) {
        if (value.isEmpty()) return Map.of();
        Map<String, String> result = new HashMap<>();
        for (String entry : value.split(",", -1)) {
            String[] parts = entry.split(":", -1);
            if (parts.length != 2) throw new IllegalArgumentException("Invalid attributes");
            result.put(decode(parts[0]), decode(parts[1]));
        }
        return Map.copyOf(result);
    }
}
