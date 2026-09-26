package de.dn.enterprise.platform.execution.domain;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record ExecutionMetadata(
        String name,
        String description,
        Map<String, String> attributes) {

    public ExecutionMetadata {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (description == null) {
            throw new IllegalArgumentException("description must not be null");
        }
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static ExecutionMetadata of(String name, String description) {
        return new ExecutionMetadata(name, description, Map.of());
    }

    public Optional<String> attribute(String key) {
        Objects.requireNonNull(key, "key must not be null");
        return Optional.ofNullable(attributes.get(key));
    }
}
