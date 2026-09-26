package de.dn.enterprise.platform.execution.domain;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record ExecutionResultStore(Map<String, ExecutionResult> results) {

    public ExecutionResultStore {
        results = results == null ? Map.of() : Map.copyOf(results);
    }

    public static ExecutionResultStore empty() {
        return new ExecutionResultStore(Map.of());
    }

    public Optional<ExecutionResult> get(String stepKey) {
        Objects.requireNonNull(stepKey, "stepKey must not be null");
        return Optional.ofNullable(results.get(stepKey));
    }

    public ExecutionResultStore with(String stepKey, ExecutionResult result) {
        Objects.requireNonNull(stepKey, "stepKey must not be null");
        Objects.requireNonNull(result, "result must not be null");

        var updated = new java.util.HashMap<>(results);
        updated.put(stepKey, result);
        return new ExecutionResultStore(updated);
    }
}
