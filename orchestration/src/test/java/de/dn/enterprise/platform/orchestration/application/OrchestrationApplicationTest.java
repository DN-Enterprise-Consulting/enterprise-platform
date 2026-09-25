package de.dn.enterprise.platform.orchestration.application;

import de.dn.enterprise.platform.orchestration.domain.Orchestration;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationMetadata;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStatus;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStep;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStepStatus;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStepType;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationType;
import de.dn.enterprise.platform.orchestration.inmemory.InMemoryOrchestrationRepository;
import de.dn.enterprise.platform.orchestration.query.OrchestrationQuery;
import de.dn.enterprise.platform.orchestration.query.OrchestrationQueryService;
import de.dn.enterprise.platform.orchestration.service.PersistentOrchestrationService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrchestrationApplicationTest {

    private static final List<OrchestrationStep> ASSESSMENT_STEP = List.of(
            new OrchestrationStep(1, OrchestrationStepType.ASSESSMENT, OrchestrationStepStatus.PENDING));

    private static OrchestrationApplication application() {
        var repository = new InMemoryOrchestrationRepository();
        var service = new PersistentOrchestrationService(repository);
        return new OrchestrationApplication(service, new OrchestrationQueryService(repository));
    }

    private static OrchestrationMetadata metadata(String name) {
        return new OrchestrationMetadata(name, name, java.util.Map.of());
    }

    @Test
    void exposesStableCreateAndGetUseCases() {
        OrchestrationApplication application = application();

        Orchestration created = application.create(
                OrchestrationType.FULL_ASSESSMENT, metadata("Assessment"), ASSESSMENT_STEP);

        assertThat(application.get(created.id())).isEqualTo(created);
    }

    @Test
    void exposesQueryUseCases() {
        OrchestrationApplication application = application();

        Orchestration assessment = application.create(
                OrchestrationType.FULL_ASSESSMENT, metadata("Assessment"), ASSESSMENT_STEP);
        Orchestration knowledge = application.create(
                OrchestrationType.ASSESSMENT_TO_KNOWLEDGE, metadata("Knowledge"), ASSESSMENT_STEP);
        Orchestration completed = application.complete(application.start(knowledge.id()).id());

        assertThat(application.findAll()).containsExactlyInAnyOrder(assessment, completed);
        assertThat(application.findByType(OrchestrationType.FULL_ASSESSMENT)).containsExactly(assessment);
        assertThat(application.findByStatus(OrchestrationStatus.COMPLETED)).containsExactly(completed);
        assertThat(application.query(OrchestrationQuery.byType(OrchestrationType.ASSESSMENT_TO_KNOWLEDGE)))
                .containsExactly(completed);
    }

    @Test
    void exposesLifecycleAndUpdateUseCases() {
        OrchestrationApplication application = application();

        Orchestration created = application.create(
                OrchestrationType.ASSESSMENT_TO_PUBLICATION, metadata("Old"), ASSESSMENT_STEP);
        Orchestration updated = application.updateMetadata(created.id(), metadata("New"));
        Orchestration started = application.start(updated.id());
        Orchestration completed = application.complete(started.id());

        assertThat(updated.metadata().name()).isEqualTo("New");
        assertThat(started.status()).isEqualTo(OrchestrationStatus.RUNNING);
        assertThat(completed.status()).isEqualTo(OrchestrationStatus.COMPLETED);
    }

    @Test
    void infrastructureIsNotRequiredByApplicationConstruction() {
        assertThatThrownBy(() -> new OrchestrationApplication(null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("service must not be null");
    }
}
