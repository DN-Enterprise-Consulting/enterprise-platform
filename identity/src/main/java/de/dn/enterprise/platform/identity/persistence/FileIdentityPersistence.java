package de.dn.enterprise.platform.identity.persistence;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityId;
import de.dn.enterprise.platform.identity.domain.IdentityMetadata;
import de.dn.enterprise.platform.identity.domain.IdentityStatus;
import de.dn.enterprise.platform.identity.domain.IdentityType;
import de.dn.enterprise.platform.identity.spi.IdentityPersistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * File-system based implementation of the IdentityPersistence SPI.
 *
 * Values containing free-form text are Base64 encoded so that arbitrary UTF-8
 * content does not interfere with the line-oriented storage format.
 */
public final class FileIdentityPersistence implements IdentityPersistence {

    private static final String FILE_EXTENSION = ".identity";

    private final Path rootDirectory;

    public FileIdentityPersistence(Path rootDirectory) {
        this.rootDirectory = Objects.requireNonNull(
                rootDirectory, "rootDirectory must not be null");
    }

    @Override
    public void save(Identity identity) {
        Objects.requireNonNull(identity, "identity must not be null");

        try {
            Files.createDirectories(rootDirectory);
            Files.write(
                    fileFor(identity.id()),
                    serialize(identity),
                    StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IdentityPersistenceException(
                    "Could not save identity " + identity.id(), exception);
        }
    }

    @Override
    public Identity load(IdentityId id) {
        Objects.requireNonNull(id, "id must not be null");

        Path file = fileFor(id);
        if (!Files.exists(file)) {
            return null;
        }

        try {
            return deserialize(
                    Files.readAllLines(file, StandardCharsets.UTF_8),
                    file);
        } catch (IOException exception) {
            throw new IdentityPersistenceException(
                    "Could not load identity " + id, exception);
        }
    }

    /**
     * Loads all valid identity records from the storage directory.
     * This is a concrete file-persistence capability used by the persistent
     * repository for bulk retrieval.
     */
    public List<Identity> loadAll() {
        if (!Files.exists(rootDirectory)) {
            return List.of();
        }

        try (var files = Files.list(rootDirectory)) {
            return files
                    .filter(path -> path.getFileName().toString().endsWith(FILE_EXTENSION))
                    .sorted()
                    .map(this::loadFromFile)
                    .toList();
        } catch (IOException exception) {
            throw new IdentityPersistenceException(
                    "Could not load identities from " + rootDirectory, exception);
        }
    }

    private Identity loadFromFile(Path file) {
        try {
            return deserialize(
                    Files.readAllLines(file, StandardCharsets.UTF_8),
                    file);
        } catch (IOException exception) {
            throw new IdentityPersistenceException(
                    "Could not load identity file " + file, exception);
        }
    }

    @Override
    public boolean exists(IdentityId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Files.exists(fileFor(id));
    }

    @Override
    public void delete(IdentityId id) {
        Objects.requireNonNull(id, "id must not be null");

        try {
            Files.deleteIfExists(fileFor(id));
        } catch (IOException exception) {
            throw new IdentityPersistenceException(
                    "Could not delete identity " + id, exception);
        }
    }

    private Path fileFor(IdentityId id) {
        return rootDirectory.resolve(id.value() + FILE_EXTENSION);
    }

    private List<String> serialize(Identity identity) {
        List<String> lines = new ArrayList<>();
        lines.add(encode(identity.id().value().toString()));
        lines.add(identity.type().name());
        lines.add(identity.status().name());
        lines.add(encode(identity.metadata().name()));
        lines.add(encode(identity.metadata().description()));
        return lines;
    }

    private Identity deserialize(List<String> lines, Path source) {
        if (lines.size() != 5) {
            throw new IdentityPersistenceException(
                    "Invalid identity file: expected exactly 5 lines: " + source);
        }

        try {
            IdentityId id = IdentityId.of(decode(lines.get(0)));
            IdentityType type = IdentityType.valueOf(lines.get(1));
            IdentityStatus status = IdentityStatus.valueOf(lines.get(2));
            String name = decode(lines.get(3));
            String description = decode(lines.get(4));

            return new Identity(
                    id,
                    type,
                    status,
                    IdentityMetadata.of(name, description));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            throw new IdentityPersistenceException(
                    "Invalid identity data in: " + source, exception);
        }
    }

    private static String encode(String value) {
        return Base64.getEncoder()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(
                Base64.getDecoder().decode(value),
                StandardCharsets.UTF_8);
    }
}
