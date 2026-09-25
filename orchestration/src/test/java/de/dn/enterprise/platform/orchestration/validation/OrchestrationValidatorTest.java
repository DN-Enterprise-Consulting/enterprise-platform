package de.dn.enterprise.platform.orchestration.validation;

import de.dn.enterprise.platform.orchestration.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class OrchestrationValidatorTest {
    private static Orchestration valid() {
        return Orchestration.draft(OrchestrationType.FULL_ASSESSMENT,
                new OrchestrationMetadata("Assessment", "desc", Map.of("owner", "dn")),
                List.of(OrchestrationStep.pending(1, OrchestrationStepType.ASSESSMENT),
                        OrchestrationStep.pending(2, OrchestrationStepType.RULE_EVALUATION)));
    }

    @Test void acceptsValidOrchestration() { assertThatCode(() -> new OrchestrationValidator().validate(valid())).doesNotThrowAnyException(); }
    @Test void validatesMetadata() {
        var validator = new OrchestrationValidator();
        assertThatThrownBy(() -> validator.validateMetadata(new OrchestrationMetadata("x", null, Map.of())))
                .isInstanceOf(OrchestrationValidationException.class);
    }
    @Test void rejectsNonContiguousStepsAtDomainBoundary() {
        assertThatThrownBy(() -> new Orchestration(OrchestrationId.newId(), OrchestrationType.FULL_ASSESSMENT,
                OrchestrationStatus.DRAFT, new OrchestrationMetadata("x", "d", Map.of()),
                List.of(OrchestrationStep.pending(1, OrchestrationStepType.ASSESSMENT),
                        OrchestrationStep.pending(3, OrchestrationStepType.PUBLICATION))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("steps must have contiguous sequence numbers starting at 1");
    }
}
