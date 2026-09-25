package de.dn.enterprise.platform.workflow.domain;

import java.util.Objects;

public record Workflow(
        WorkflowId id,
        WorkflowType type,
        WorkflowStatus status,
        WorkflowMetadata metadata) {

    public Workflow {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(metadata, "metadata must not be null");
    }

    public static Workflow draft(
            WorkflowType type,
            WorkflowMetadata metadata) {
        return new Workflow(
                WorkflowId.newId(),
                type,
                WorkflowStatus.DRAFT,
                metadata);
    }

    public Workflow withStatus(WorkflowStatus status) {
        return new Workflow(id, type, status, metadata);
    }

    public Workflow withMetadata(WorkflowMetadata metadata) {
        return new Workflow(id, type, status, metadata);
    }
}
