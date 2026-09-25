package de.dn.enterprise.platform.orchestration.service;

import de.dn.enterprise.platform.orchestration.domain.*;
import de.dn.enterprise.platform.orchestration.inmemory.InMemoryOrchestrationRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class PersistentOrchestrationServiceTest {

    private static final OrchestrationMetadata META = new OrchestrationMetadata("Assessment", "desc", Map.of("owner", "dn"));
    private static final List<OrchestrationStep> STEPS = List.of(
            OrchestrationStep.pending(1, OrchestrationStepType.ASSESSMENT),
            OrchestrationStep.pending(2, OrchestrationStepType.RULE_EVALUATION));

    @Test
    void createAndGet() {
        var service = new PersistentOrchestrationService(new InMemoryOrchestrationRepository());
        var created = service.create(OrchestrationType.FULL_ASSESSMENT, META, STEPS);
        assertThat(created.status()).isEqualTo(OrchestrationStatus.DRAFT);
        assertThat(service.get(created.id())).isEqualTo(created);
    }

    @Test
    void lifecycleAllowsDraftToRunningToCompleted() {
        var service = new PersistentOrchestrationService(new InMemoryOrchestrationRepository());
        var created = service.create(OrchestrationType.FULL_ASSESSMENT, META, STEPS);
        assertThat(service.start(created.id()).status()).isEqualTo(OrchestrationStatus.RUNNING);
        assertThat(service.complete(created.id()).status()).isEqualTo(OrchestrationStatus.COMPLETED);
    }

    @Test
    void lifecycleAllowsRunningToFailed() {
        var service = new PersistentOrchestrationService(new InMemoryOrchestrationRepository());
        var created = service.create(OrchestrationType.ASSESSMENT_TO_KNOWLEDGE, META, STEPS);
        service.start(created.id());
        assertThat(service.fail(created.id()).status()).isEqualTo(OrchestrationStatus.FAILED);
    }

    @Test
    void invalidTransitionsAreRejectedAndTerminalStatesRemainTerminal() {
        var service = new PersistentOrchestrationService(new InMemoryOrchestrationRepository());
        var created = service.create(OrchestrationType.ASSESSMENT_TO_PUBLICATION, META, STEPS);
        assertThatThrownBy(() -> service.complete(created.id()))
                .isInstanceOf(OrchestrationLifecycleException.class);
        service.start(created.id());
        service.complete(created.id());
        assertThatThrownBy(() -> service.start(created.id()))
                .isInstanceOf(OrchestrationLifecycleException.class);
        assertThatThrownBy(() -> service.fail(created.id()))
                .isInstanceOf(OrchestrationLifecycleException.class);
    }

    @Test
    void metadataCanOnlyBeChangedWhileDraft() {
        var service = new PersistentOrchestrationService(new InMemoryOrchestrationRepository());
        var created = service.create(OrchestrationType.FULL_ASSESSMENT, META, STEPS);
        var changed = service.updateMetadata(created.id(), new OrchestrationMetadata("Changed", "new", Map.of()));
        assertThat(changed.metadata().name()).isEqualTo("Changed");
        service.start(created.id());
        assertThatThrownBy(() -> service.updateMetadata(created.id(), META))
                .isInstanceOf(OrchestrationLifecycleException.class);
    }

    @Test
    void missingOrchestrationIsRejected() {
        var service = new PersistentOrchestrationService(new InMemoryOrchestrationRepository());
        var id = OrchestrationId.newId();
        assertThatThrownBy(() -> service.get(id)).isInstanceOf(OrchestrationNotFoundException.class);
        assertThatThrownBy(() -> service.start(id)).isInstanceOf(OrchestrationNotFoundException.class);
    }

    @Test
    void queryMethodsDelegateToRepository() {
        var service = new PersistentOrchestrationService(new InMemoryOrchestrationRepository());
        service.create(OrchestrationType.FULL_ASSESSMENT, META, STEPS);
        service.create(OrchestrationType.ASSESSMENT_TO_KNOWLEDGE, META, STEPS);
        assertThat(service.findAll()).hasSize(2);
        assertThat(service.findByType(OrchestrationType.FULL_ASSESSMENT)).hasSize(1);
        assertThat(service.findByStatus(OrchestrationStatus.DRAFT)).hasSize(2);
    }
}
