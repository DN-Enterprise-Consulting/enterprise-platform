package de.dn.enterprise.platform.orchestration.persistence;

import de.dn.enterprise.platform.orchestration.domain.Orchestration;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationId;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationMetadata;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStatus;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStep;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStepStatus;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStepType;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationType;
import de.dn.enterprise.platform.orchestration.spi.OrchestrationPersistence;

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
import java.util.UUID;
import java.util.stream.Collectors;

public final class FileOrchestrationPersistence implements OrchestrationPersistence {

    private static final String EXTENSION = ".orchestration";

    private final Path storageDirectory;

    public FileOrchestrationPersistence(Path storageDirectory) {
        this.storageDirectory = Objects.requireNonNull(storageDirectory, "storageDirectory must not be null");
    }

    @Override
    public void save(Orchestration orchestration) {
        Objects.requireNonNull(orchestration, "orchestration must not be null");
        try {
            Files.createDirectories(storageDirectory);
            Files.writeString(fileFor(orchestration.id()), serialize(orchestration), StandardCharsets.UTF_8);
        } catch (IOException | RuntimeException exception) {
            throw new OrchestrationPersistenceException(
                    "Could not save orchestration " + orchestration.id(), exception);
        }
    }

    @Override
    public Orchestration load(OrchestrationId id) {
        Objects.requireNonNull(id, "id must not be null");
        Path file = fileFor(id);
        if (!Files.exists(file)) {
            return null;
        }

        try {
            return deserialize(Files.readAllLines(file, StandardCharsets.UTF_8));
        } catch (IOException | RuntimeException exception) {
            throw new OrchestrationPersistenceException(
                    "Could not load orchestration " + id, exception);
        }
    }

    /**
     * Loads all persisted orchestrations from the storage directory.
     * This is intentionally an implementation-level extension used by the
     * file-backed repository until the persistence SPI exposes loadAll().
     */
    public List<Orchestration> loadAll() {
        if (!Files.exists(storageDirectory)) {
            return List.of();
        }

        try {
            List<Orchestration> orchestrations = new ArrayList<>();
            try (var stream = Files.list(storageDirectory)) {
                List<Path> files = stream
                        .filter(path -> path.getFileName().toString().endsWith(EXTENSION))
                        .sorted()
                        .toList();
                for (Path file : files) {
                    try {
                        orchestrations.add(deserialize(
                                Files.readAllLines(file, StandardCharsets.UTF_8)));
                    } catch (IOException | RuntimeException exception) {
                        throw new OrchestrationPersistenceException(
                                "Could not load orchestration file " + file, exception);
                    }
                }
            }
            return List.copyOf(orchestrations);
        } catch (IOException | OrchestrationPersistenceException exception) {
            if (exception instanceof OrchestrationPersistenceException persistenceException) {
                throw persistenceException;
            }
            throw new OrchestrationPersistenceException(
                    "Could not enumerate orchestration storage " + storageDirectory, exception);
        }
    }

    @Override
    public boolean exists(OrchestrationId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Files.exists(fileFor(id));
    }

    @Override
    public void delete(OrchestrationId id) {
        Objects.requireNonNull(id, "id must not be null");
        try {
            Files.deleteIfExists(fileFor(id));
        } catch (IOException | RuntimeException exception) {
            throw new OrchestrationPersistenceException(
                    "Could not delete orchestration " + id, exception);
        }
    }

    private Path fileFor(OrchestrationId id) {
        return storageDirectory.resolve(id.value() + EXTENSION);
    }

    private static String serialize(Orchestration orchestration) {
        String attributes = orchestration.metadata().attributes().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> encode(entry.getKey()) + ":" + encode(entry.getValue()))
                .collect(Collectors.joining(";"));

        String steps = orchestration.steps().stream()
                .map(step -> step.sequence() + "," + step.type().name() + "," + step.status().name())
                .collect(Collectors.joining(";"));

        return String.join("\n",
                encode(orchestration.id().value().toString()),
                encode(orchestration.type().name()),
                encode(orchestration.status().name()),
                encode(orchestration.metadata().name()),
                encode(orchestration.metadata().description() == null ? "" : orchestration.metadata().description()),
                encode(attributes),
                encode(steps));
    }

    private static Orchestration deserialize(List<String> lines) {
        if (lines.size() != 7) {
            throw new IllegalArgumentException("Invalid orchestration file: expected 7 lines");
        }

        UUID id = UUID.fromString(decode(lines.get(0)));
        OrchestrationType type = OrchestrationType.valueOf(decode(lines.get(1)));
        OrchestrationStatus status = OrchestrationStatus.valueOf(decode(lines.get(2)));
        String name = decode(lines.get(3));
        String description = decode(lines.get(4));

        Map<String, String> attributes = decodeAttributes(decode(lines.get(5)));
        List<OrchestrationStep> steps = decodeSteps(decode(lines.get(6)));

        return new Orchestration(
                new OrchestrationId(id),
                type,
                status,
                new OrchestrationMetadata(name, description, attributes),
                steps);
    }

    private static Map<String, String> decodeAttributes(String value) {
        if (value.isEmpty()) {
            return Map.of();
        }

        return java.util.Arrays.stream(value.split(";", -1))
                .map(entry -> entry.split(":", 2))
                .collect(Collectors.toMap(
                        parts -> decode(parts[0]),
                        parts -> decode(parts[1])));
    }

    private static List<OrchestrationStep> decodeSteps(String value) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Orchestration must contain steps");
        }

        List<OrchestrationStep> steps = new ArrayList<>();
        for (String entry : value.split(";", -1)) {
            String[] parts = entry.split(",", -1);
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid orchestration step");
            }
            steps.add(new OrchestrationStep(
                    Integer.parseInt(parts[0]),
                    OrchestrationStepType.valueOf(parts[1]),
                    OrchestrationStepStatus.valueOf(parts[2])));
        }
        steps.sort(Comparator.comparingInt(OrchestrationStep::sequence));
        return List.copyOf(steps);
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
