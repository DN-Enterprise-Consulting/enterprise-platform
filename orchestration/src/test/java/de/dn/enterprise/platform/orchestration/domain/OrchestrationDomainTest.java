package de.dn.enterprise.platform.orchestration.domain;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
class OrchestrationDomainTest {
    @Test void createsOrderedDraft() {
        var o = Orchestration.draft(OrchestrationType.FULL_ASSESSMENT,
            new OrchestrationMetadata("Full Assessment","test",Map.of()),
            List.of(OrchestrationStep.pending(1,OrchestrationStepType.ASSESSMENT),
                     OrchestrationStep.pending(2,OrchestrationStepType.RULE_EVALUATION),
                     OrchestrationStep.pending(3,OrchestrationStepType.KNOWLEDGE_PROCESSING),
                     OrchestrationStep.pending(4,OrchestrationStepType.PUBLICATION)));
        assertThat(o.status()).isEqualTo(OrchestrationStatus.DRAFT);
        assertThat(o.steps()).hasSize(4);
    }
    @Test void rejectsEmptySteps() {
        assertThatThrownBy(() -> Orchestration.draft(OrchestrationType.FULL_ASSESSMENT,
            new OrchestrationMetadata("x","y",Map.of()),List.of())).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void rejectsInvalidSequence() {
        assertThatThrownBy(() -> new Orchestration(OrchestrationId.newId(),OrchestrationType.FULL_ASSESSMENT,
            OrchestrationStatus.DRAFT,new OrchestrationMetadata("x","y",Map.of()),
            List.of(OrchestrationStep.pending(2,OrchestrationStepType.ASSESSMENT))))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
