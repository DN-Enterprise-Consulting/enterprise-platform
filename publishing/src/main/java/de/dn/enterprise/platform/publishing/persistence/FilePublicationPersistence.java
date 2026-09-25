package de.dn.enterprise.platform.publishing.persistence;

import de.dn.enterprise.platform.publishing.domain.Publication;
import de.dn.enterprise.platform.publishing.domain.PublicationId;
import de.dn.enterprise.platform.publishing.domain.PublicationMetadata;
import de.dn.enterprise.platform.publishing.domain.PublicationStatus;
import de.dn.enterprise.platform.publishing.domain.PublicationType;
import de.dn.enterprise.platform.publishing.spi.PublicationPersistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class FilePublicationPersistence implements PublicationPersistence {

    private static final String FILE_EXTENSION = ".publication";
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    private final Path rootDirectory;

    public FilePublicationPersistence(Path rootDirectory) {
        this.rootDirectory = Objects.requireNonNull(
                rootDirectory, "rootDirectory must not be null");
    }

    @Override
    public Publication save(Publication publication) {
        Objects.requireNonNull(publication, "publication must not be null");
        try {
            Files.createDirectories(rootDirectory);
            Files.write(fileFor(publication.id()), serialize(publication),
                    StandardCharsets.UTF_8);
            return publication;
        } catch (IOException exception) {
            throw new PublicationPersistenceException(
                    "Could not save publication " + publication.id(), exception);
        }
    }

    @Override
    public Optional<Publication> load(PublicationId id) {
        Objects.requireNonNull(id, "id must not be null");
        Path file = fileFor(id);
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        try {
            return Optional.of(deserialize(
                    Files.readAllLines(file, StandardCharsets.UTF_8)));
        } catch (IOException exception) {
            throw new PublicationPersistenceException(
                    "Could not load publication " + id, exception);
        }
    }

    public List<Publication> loadAll() {
        try {
            if (!Files.exists(rootDirectory)) {
                return List.of();
            }
            try (var files = Files.list(rootDirectory)) {
                return files
                        .filter(path -> path.getFileName().toString()
                                .endsWith(FILE_EXTENSION))
                        .sorted()
                        .map(this::loadFile)
                        .flatMap(Optional::stream)
                        .toList();
            }
        } catch (IOException exception) {
            throw new PublicationPersistenceException(
                    "Could not load publications from " + rootDirectory,
                    exception);
        }
    }

    private Optional<Publication> loadFile(Path file) {
        try {
            return Optional.of(deserialize(
                    Files.readAllLines(file, StandardCharsets.UTF_8)));
        } catch (IOException exception) {
            throw new PublicationPersistenceException(
                    "Could not load publication file " + file, exception);
        }
    }

    @Override
    public boolean exists(PublicationId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Files.exists(fileFor(id));
    }

    @Override
    public void delete(PublicationId id) {
        Objects.requireNonNull(id, "id must not be null");
        try {
            Files.deleteIfExists(fileFor(id));
        } catch (IOException exception) {
            throw new PublicationPersistenceException(
                    "Could not delete publication " + id, exception);
        }
    }

    private Path fileFor(PublicationId id) {
        return rootDirectory.resolve(id.value() + FILE_EXTENSION);
    }

    private List<String> serialize(Publication publication) {
        List<String> lines = new ArrayList<>();
        lines.add(encode(publication.id().value().toString()));
        lines.add(publication.type().name());
        lines.add(publication.status().name());
        lines.add(encode(publication.metadata().name()));
        lines.add(encode(publication.metadata().description()));

        publication.metadata().attributes().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    lines.add(encode(entry.getKey()));
                    lines.add(encode(entry.getValue()));
                });
        return lines;
    }

    private Publication deserialize(List<String> lines) {
        if (lines.size() < 5) {
            throw new PublicationPersistenceException(
                    "Invalid publication file: expected at least 5 lines", null);
        }
        try {
            PublicationId id = PublicationId.parse(decode(lines.get(0)));
            PublicationType type = PublicationType.valueOf(lines.get(1));
            PublicationStatus status = PublicationStatus.valueOf(lines.get(2));
            String name = decode(lines.get(3));
            String description = decode(lines.get(4));

            if ((lines.size() - 5) % 2 != 0) {
                throw new IllegalArgumentException(
                        "attribute lines must occur in key/value pairs");
            }

            LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
            for (int index = 5; index < lines.size(); index += 2) {
                attributes.put(
                        decode(lines.get(index)),
                        decode(lines.get(index + 1)));
            }

            PublicationMetadata metadata =
                    new PublicationMetadata(name, description, attributes);
            return new Publication(id, type, status, metadata);
        } catch (RuntimeException exception) {
            if (exception instanceof PublicationPersistenceException persistenceException) {
                throw persistenceException;
            }
            throw new PublicationPersistenceException(
                    "Invalid publication file content", exception);
        }
    }

    private String encode(String value) {
        return ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        return new String(DECODER.decode(value), StandardCharsets.UTF_8);
    }
}
