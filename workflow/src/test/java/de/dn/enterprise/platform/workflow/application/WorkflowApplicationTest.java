package de.dn.enterprise.platform.workflow.application;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.domain.WorkflowMetadata;
import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;
import de.dn.enterprise.platform.workflow.inmemory.InMemoryWorkflowRepository;
import de.dn.enterprise.platform.workflow.service.PersistentWorkflowService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkflowApplicationTest {

    @Test
    void exposesStableCreateAndGetUseCases() {
        WorkflowApplication application = new WorkflowApplication(
                new PersistentWorkflowService(new InMemoryWorkflowRepository()));

        Workflow created = application.create(
                WorkflowType.ASSESSMENT,
                WorkflowMetadata.of("Assessment"));

        assertThat(application.get(created.id())).isEqualTo(created);
    }

    @Test
    void exposesQueryUseCases() {
        WorkflowApplication application = new WorkflowApplication(
                new PersistentWorkflowService(new InMemoryWorkflowRepository()));

        Workflow assessment = application.create(
                WorkflowType.ASSESSMENT, WorkflowMetadata.of("Assessment"));
        Workflow knowledge = application.create(
                WorkflowType.KNOWLEDGE_PROCESSING, WorkflowMetadata.of("Knowledge"));
        Workflow completed = application.complete(application.start(knowledge.id()).id());

        assertThat(application.findAll()).containsExactlyInAnyOrder(assessment, completed);
        assertThat(application.findByType(WorkflowType.ASSESSMENT)).containsExactly(assessment);
        assertThat(application.findByStatus(WorkflowStatus.COMPLETED)).containsExactly(completed);
    }

    @Test
    void exposesLifecycleAndUpdateUseCases() {
        WorkflowApplication application = new WorkflowApplication(
                new PersistentWorkflowService(new InMemoryWorkflowRepository()));

        Workflow created = application.create(
                WorkflowType.PUBLICATION, WorkflowMetadata.of("Old"));
        Workflow updated = application.updateMetadata(
                created.id(), WorkflowMetadata.of("New"));
        Workflow started = application.start(updated.id());
        Workflow completed = application.complete(started.id());

        assertThat(updated.metadata().name()).isEqualTo("New");
        assertThat(started.status()).isEqualTo(WorkflowStatus.RUNNING);
        assertThat(completed.status()).isEqualTo(WorkflowStatus.COMPLETED);
    }

    @Test
    void infrastructureIsNotRequiredByApplicationConstruction() {
        assertThatThrownBy(() -> new WorkflowApplication(null))
                .isInstanceOf(NullPointerException.class);
    }
}
