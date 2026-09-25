package de.dn.enterprise.platform.orchestration.domain;
import java.util.List;
import java.util.Objects;
public record Orchestration(
        OrchestrationId id, OrchestrationType type, OrchestrationStatus status,
        OrchestrationMetadata metadata, List<OrchestrationStep> steps) {
    public Orchestration {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(metadata, "metadata must not be null");
        Objects.requireNonNull(steps, "steps must not be null");
        steps = List.copyOf(steps);
        if (steps.isEmpty()) throw new IllegalArgumentException("steps must not be empty");
        for (int i = 0; i < steps.size(); i++)
            if (steps.get(i).sequence() != i + 1)
                throw new IllegalArgumentException("steps must have contiguous sequence numbers starting at 1");
    }
    public static Orchestration draft(OrchestrationType type, OrchestrationMetadata metadata, List<OrchestrationStep> steps) {
        return new Orchestration(OrchestrationId.newId(), type, OrchestrationStatus.DRAFT, metadata, steps);
    }
}
