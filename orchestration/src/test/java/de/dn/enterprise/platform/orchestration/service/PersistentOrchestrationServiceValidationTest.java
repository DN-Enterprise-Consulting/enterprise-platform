package de.dn.enterprise.platform.orchestration.service;

import de.dn.enterprise.platform.orchestration.domain.*;
import de.dn.enterprise.platform.orchestration.inmemory.InMemoryOrchestrationRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class PersistentOrchestrationServiceValidationTest {
    private static final OrchestrationMetadata META = new OrchestrationMetadata("Assessment", "desc", Map.of("owner", "dn"));
    private static final List<OrchestrationStep> STEPS = List.of(
            OrchestrationStep.pending(1, OrchestrationStepType.ASSESSMENT),
            OrchestrationStep.pending(2, OrchestrationStepType.RULE_EVALUATION));

    @Test void validatesCreatedObjectAndLifecycleResults() {
        var service = new PersistentOrchestrationService(new InMemoryOrchestrationRepository());
        var created = service.create(OrchestrationType.FULL_ASSESSMENT, META, STEPS);
        assertThat(service.start(created.id()).status()).isEqualTo(OrchestrationStatus.RUNNING);
        assertThat(service.complete(created.id()).status()).isEqualTo(OrchestrationStatus.COMPLETED);
    }

    @Test void rejectsNullDependencies() {
        assertThatThrownBy(() -> new PersistentOrchestrationService(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PersistentOrchestrationService(new InMemoryOrchestrationRepository(), null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test void validatesRetrievedObjects() {
        var service = new PersistentOrchestrationService(new InMemoryOrchestrationRepository());
        var created = service.create(OrchestrationType.FULL_ASSESSMENT, META, STEPS);
        assertThat(service.get(created.id())).isEqualTo(created);
        assertThat(service.findAll()).containsExactly(created);
        assertThat(service.findByType(OrchestrationType.FULL_ASSESSMENT)).containsExactly(created);
        assertThat(service.findByStatus(OrchestrationStatus.DRAFT)).containsExactly(created);
    }
}
