package de.dn.enterprise.platform.workflow.domain;

import java.util.Map;
import java.util.Objects;

public record WorkflowMetadata(
        String name,
        String description,
        Map<String, String> attributes) {

    public WorkflowMetadata {
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }

        description = description == null ? "" : description;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static WorkflowMetadata of(String name) {
        return new WorkflowMetadata(name, "", Map.of());
    }
}
