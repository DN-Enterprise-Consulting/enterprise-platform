package de.dn.enterprise.platform.assessment.persistence;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentId;
import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import de.dn.enterprise.platform.assessment.spi.AssessmentPersistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class FileAssessmentPersistence implements AssessmentPersistence {

    private static final String EXTENSION = ".assessment";
        private final Path storageDirectory;

    public FileAssessmentPersistence(Path storageDirectory) {
        this.storageDirectory = Objects.requireNonNull(storageDirectory, "storageDirectory must not be null");
    }

    @Override
    public Assessment save(Assessment assessment) {
        Objects.requireNonNull(assessment, "assessment must not be null");
        ensureDirectory();

        Path file = fileFor(assessment.id());
        List<String> lines = new ArrayList<>();
        lines.add(encode(assessment.id().value().toString()));
        lines.add(assessment.type().name());
        lines.add(assessment.status().name());
        lines.add(encode(assessment.metadata().name()));
        lines.add(encode(assessment.metadata().description()));
        lines.add(encodeAttributes(assessment.metadata().attributes()));

        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
            return assessment;
        } catch (IOException exception) {
            throw new AssessmentPersistenceException("Could not save assessment " + assessment.id(), exception);
        }
    }

    @Override
    public Optional<Assessment> load(AssessmentId id) {
        Objects.requireNonNull(id, "id must not be null");

        Path file = fileFor(id);
        if (!Files.exists(file)) {
            return Optional.empty();
        }

        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            return Optional.of(deserialize(lines));
        } catch (IOException | RuntimeException exception) {
            throw new AssessmentPersistenceException("Could not load assessment " + id, exception);
        }
    }

    @Override
    public List<Assessment> loadAll() {
        ensureDirectory();

        try (var stream = Files.list(storageDirectory)) {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .map(this::loadFile)
                    .toList();
        } catch (IOException exception) {
            throw new AssessmentPersistenceException(
                    "Could not list assessments in " + storageDirectory, exception);
        }
    }

    @Override
    public boolean exists(AssessmentId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Files.exists(fileFor(id));
    }

    @Override
    public void delete(AssessmentId id) {
        Objects.requireNonNull(id, "id must not be null");

        try {
            Files.deleteIfExists(fileFor(id));
        } catch (IOException exception) {
            throw new AssessmentPersistenceException("Could not delete assessment " + id, exception);
        }
    }

    public Path storageDirectory() {
        return storageDirectory;
    }

    private Assessment loadFile(Path file) {
        try {
            return deserialize(Files.readAllLines(file, StandardCharsets.UTF_8));
        } catch (IOException | RuntimeException exception) {
            throw new AssessmentPersistenceException("Could not load assessment file " + file, exception);
        }
    }

    private Assessment deserialize(List<String> lines) {
        if (lines.size() < 6) {
            throw new AssessmentPersistenceException("Invalid assessment file: expected at least 6 lines");
        }

        AssessmentId id = AssessmentId.of(UUID.fromString(decode(lines.get(0))));
        AssessmentType type = AssessmentType.valueOf(lines.get(1));
        AssessmentStatus status = AssessmentStatus.valueOf(lines.get(2));
        String name = decode(lines.get(3));
        String description = decode(lines.get(4));
        Map<String, String> attributes = decodeAttributes(lines.get(5));

        AssessmentMetadata metadata = new AssessmentMetadata(name, description, attributes);
        return new Assessment(id, type, status, metadata);
    }

    private Path fileFor(AssessmentId id) {
        return storageDirectory.resolve(id.value() + EXTENSION);
    }

    private void ensureDirectory() {
        try {
            Files.createDirectories(storageDirectory);
        } catch (IOException exception) {
            throw new AssessmentPersistenceException(
                    "Could not create storage directory " + storageDirectory, exception);
        }
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(
                Objects.requireNonNull(value, "value must not be null")
                        .getBytes(StandardCharsets.UTF_8));
    }

    private static String encodeAttributes(Map<String, String> attributes) {
        return attributes.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> encode(entry.getKey()) + ":" + encode(entry.getValue()))
                .reduce((left, right) -> left + ";" + right)
                .orElse("");
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private static Map<String, String> decodeAttributes(String value) {
        if (value.isBlank()) {
            return Map.of();
        }

        return java.util.Arrays.stream(value.split(";"))
                .map(entry -> entry.split(":", 2))
                .collect(java.util.stream.Collectors.toUnmodifiableMap(
                        parts -> decode(parts[0]),
                        parts -> decode(parts[1])));
    }
}
