package de.dn.enterprise.platform.execution.domain;

import java.util.Objects;

public record Execution(
        ExecutionId id,
        ExecutionType type,
        ExecutionStatus status,
        ExecutionMetadata metadata) {

    public Execution {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(metadata, "metadata must not be null");
    }

    public static Execution draft(ExecutionType type, ExecutionMetadata metadata) {
        return new Execution(ExecutionId.newId(), type, ExecutionStatus.DRAFT, metadata);
    }

    public Execution start() {
        requireStatus(ExecutionStatus.DRAFT);
        return new Execution(id, type, ExecutionStatus.RUNNING, metadata);
    }

    public Execution complete() {
        requireStatus(ExecutionStatus.RUNNING);
        return new Execution(id, type, ExecutionStatus.COMPLETED, metadata);
    }

    public Execution fail() {
        requireStatus(ExecutionStatus.RUNNING);
        return new Execution(id, type, ExecutionStatus.FAILED, metadata);
    }

    public Execution updateMetadata(ExecutionMetadata newMetadata) {
        Objects.requireNonNull(newMetadata, "newMetadata must not be null");
        requireStatus(ExecutionStatus.DRAFT);
        return new Execution(id, type, status, newMetadata);
    }

    private void requireStatus(ExecutionStatus expected) {
        if (status != expected) {
            throw new IllegalStateException(
                    "execution " + id + " must be in " + expected + " but is " + status);
        }
    }
}
