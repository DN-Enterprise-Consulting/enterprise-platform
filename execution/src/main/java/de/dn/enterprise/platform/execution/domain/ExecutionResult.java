package de.dn.enterprise.platform.execution.domain;

import java.util.Map;
import java.util.Objects;

public record ExecutionResult(
        ExecutionResultStatus status,
        String summary,
        Map<String, String> outputs) {

    public ExecutionResult {
        Objects.requireNonNull(status, "status must not be null");

        if (summary == null || summary.isBlank()) {
            throw new IllegalArgumentException("summary must not be blank");
        }

        outputs = outputs == null ? Map.of() : Map.copyOf(outputs);
    }

    public static ExecutionResult success(String summary) {
        return new ExecutionResult(ExecutionResultStatus.SUCCESS, summary, Map.of());
    }

    public static ExecutionResult success(String summary, Map<String, String> outputs) {
        return new ExecutionResult(ExecutionResultStatus.SUCCESS, summary, outputs);
    }

    public static ExecutionResult failure(String summary) {
        return new ExecutionResult(ExecutionResultStatus.FAILURE, summary, Map.of());
    }

    public boolean successful() {
        return status == ExecutionResultStatus.SUCCESS;
    }
}
