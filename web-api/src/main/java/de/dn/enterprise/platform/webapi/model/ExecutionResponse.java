package de.dn.enterprise.platform.webapi.model;

import de.dn.enterprise.platform.execution.domain.ExecutionResultStatus;

import java.util.Map;
import java.util.Objects;

/** API response for one executed platform step. */
public record ExecutionResponse(
        ExecutionResultStatus status,
        boolean successful,
        String summary,
        Map<String, String> outputs) {

    public ExecutionResponse {
        Objects.requireNonNull(status, "status must not be null");
        if (summary == null || summary.isBlank()) {
            throw new IllegalArgumentException("summary must not be blank");
        }
        outputs = outputs == null ? Map.of() : Map.copyOf(outputs);
    }
}
