package de.dn.enterprise.platform.webapi.model;

import de.dn.enterprise.platform.orchestration.domain.OrchestrationStepType;

import java.util.Map;
import java.util.Objects;

/** API request for executing one platform step. */
public record ExecutionRequest(
        int sequence,
        OrchestrationStepType stepType,
        Map<String, String> context) {

    public ExecutionRequest {
        if (sequence < 1) {
            throw new IllegalArgumentException("sequence must be >= 1");
        }
        Objects.requireNonNull(stepType, "stepType must not be null");
        context = context == null ? Map.of() : Map.copyOf(context);
    }
}
