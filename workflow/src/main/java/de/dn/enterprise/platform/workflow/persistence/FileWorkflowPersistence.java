package de.dn.enterprise.platform.workflow.persistence;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.domain.WorkflowId;
import de.dn.enterprise.platform.workflow.domain.WorkflowMetadata;
import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;
import de.dn.enterprise.platform.workflow.spi.WorkflowPersistence;

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

public final class FileWorkflowPersistence implements WorkflowPersistence {

    private static final String FILE_EXTENSION = ".workflow";
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    private final Path rootDirectory;

    public FileWorkflowPersistence(Path rootDirectory) {
        this.rootDirectory = Objects.requireNonNull(
                rootDirectory, "rootDirectory must not be null");
    }

    @Override
    public Workflow save(Workflow workflow) {
        Objects.requireNonNull(workflow, "workflow must not be null");

        try {
            Files.createDirectories(rootDirectory);
            Files.write(
                    fileFor(workflow.id()),
                    serialize(workflow),
                    StandardCharsets.UTF_8);
            return workflow;
        } catch (IOException exception) {
            throw new WorkflowPersistenceException(
                    "Could not save workflow " + workflow.id(), exception);
        }
    }

    @Override
    public Optional<Workflow> load(WorkflowId id) {
        Objects.requireNonNull(id, "id must not be null");
        Path file = fileFor(id);

        if (!Files.exists(file)) {
            return Optional.empty();
        }

        try {
            return Optional.of(deserialize(
                    Files.readAllLines(file, StandardCharsets.UTF_8)));
        } catch (IOException exception) {
            throw new WorkflowPersistenceException(
                    "Could not load workflow " + id, exception);
        }
    }

    /**
     * Loads all persisted workflows in deterministic filename order.
     */
    public List<Workflow> loadAll() {
        try {
            if (!Files.exists(rootDirectory)) {
                return List.of();
            }

            try (var files = Files.list(rootDirectory)) {
                return files
                        .filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().endsWith(FILE_EXTENSION))
                        .sorted()
                        .map(path -> {
                            try {
                                return deserialize(Files.readAllLines(
                                        path, StandardCharsets.UTF_8));
                            } catch (IOException exception) {
                                throw new WorkflowPersistenceException(
                                        "Could not load workflow file " + path, exception);
                            }
                        })
                        .toList();
            }
        } catch (WorkflowPersistenceException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new WorkflowPersistenceException(
                    "Could not list workflow persistence directory " + rootDirectory, exception);
        }
    }

    @Override
    public boolean exists(WorkflowId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Files.exists(fileFor(id));
    }

    @Override
    public void delete(WorkflowId id) {
        Objects.requireNonNull(id, "id must not be null");

        try {
            Files.deleteIfExists(fileFor(id));
        } catch (IOException exception) {
            throw new WorkflowPersistenceException(
                    "Could not delete workflow " + id, exception);
        }
    }

    private Path fileFor(WorkflowId id) {
        return rootDirectory.resolve(id.value() + FILE_EXTENSION);
    }

    private List<String> serialize(Workflow workflow) {
        List<String> lines = new ArrayList<>();
        lines.add(encode(workflow.id().value().toString()));
        lines.add(workflow.type().name());
        lines.add(workflow.status().name());
        lines.add(encode(workflow.metadata().name()));
        lines.add(encode(workflow.metadata().description()));

        workflow.metadata().attributes().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    lines.add(encode(entry.getKey()));
                    lines.add(encode(entry.getValue()));
                });

        return lines;
    }

    private Workflow deserialize(List<String> lines) {
        if (lines.size() < 5) {
            throw new WorkflowPersistenceException(
                    "Invalid workflow file: expected at least 5 lines", null);
        }

        try {
            WorkflowId id = WorkflowId.parse(decode(lines.get(0)));
            WorkflowType type = WorkflowType.valueOf(lines.get(1));
            WorkflowStatus status = WorkflowStatus.valueOf(lines.get(2));
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

            WorkflowMetadata metadata =
                    new WorkflowMetadata(name, description, attributes);

            return new Workflow(id, type, status, metadata);
        } catch (RuntimeException exception) {
            if (exception instanceof WorkflowPersistenceException persistenceException) {
                throw persistenceException;
            }
            throw new WorkflowPersistenceException(
                    "Invalid workflow file content", exception);
        }
    }

    private String encode(String value) {
        return ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        return new String(DECODER.decode(value), StandardCharsets.UTF_8);
    }
}
