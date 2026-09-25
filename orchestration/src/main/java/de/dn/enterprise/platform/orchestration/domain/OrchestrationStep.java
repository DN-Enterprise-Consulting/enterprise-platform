package de.dn.enterprise.platform.orchestration.domain;
import java.util.Objects;
public record OrchestrationStep(int sequence, OrchestrationStepType type, OrchestrationStepStatus status) {
    public OrchestrationStep {
        if (sequence < 1) throw new IllegalArgumentException("sequence must be >= 1");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(status, "status must not be null");
    }
    public static OrchestrationStep pending(int sequence, OrchestrationStepType type) {
        return new OrchestrationStep(sequence, type, OrchestrationStepStatus.PENDING);
    }
}
