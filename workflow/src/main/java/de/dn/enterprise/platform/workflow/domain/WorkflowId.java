package de.dn.enterprise.platform.workflow.domain;

import java.util.Objects;
import java.util.UUID;

public record WorkflowId(UUID value) {

    public WorkflowId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static WorkflowId newId() {
        return new WorkflowId(UUID.randomUUID());
    }

    public static WorkflowId of(UUID value) {
        return new WorkflowId(value);
    }

    public static WorkflowId parse(String value) {
        Objects.requireNonNull(value, "value must not be null");
        return new WorkflowId(UUID.fromString(value));
    }
}
