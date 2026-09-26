package de.dn.enterprise.platform.execution.domain;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record ExecutionContext(Map<String, String> values) {

    public ExecutionContext {
        values = values == null ? Map.of() : Map.copyOf(values);
    }

    public static ExecutionContext empty() {
        return new ExecutionContext(Map.of());
    }

    public Optional<String> get(String key) {
        Objects.requireNonNull(key, "key must not be null");
        return Optional.ofNullable(values.get(key));
    }

    public ExecutionContext with(String key, String value) {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(value, "value must not be null");

        var updated = new java.util.HashMap<>(values);
        updated.put(key, value);
        return new ExecutionContext(updated);
    }

    public ExecutionContext without(String key) {
        Objects.requireNonNull(key, "key must not be null");

        var updated = new java.util.HashMap<>(values);
        updated.remove(key);
        return new ExecutionContext(updated);
    }
}
