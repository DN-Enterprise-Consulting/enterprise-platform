package de.dn.enterprise.platform.workflow.service;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.domain.WorkflowMetadata;
import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;
import de.dn.enterprise.platform.workflow.inmemory.InMemoryWorkflowRepository;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersistentWorkflowServiceTest {

    @Test
    void createsDraftWorkflow() {
        PersistentWorkflowService service = service();
        Workflow workflow = service.create(WorkflowType.ASSESSMENT, metadata("Assessment"));
        assertThat(workflow.status()).isEqualTo(WorkflowStatus.DRAFT);
        assertThat(service.get(workflow.id())).isEqualTo(workflow);
    }

    @Test
    void updatesMetadataOnlyWhileDraft() {
        PersistentWorkflowService service = service();
        Workflow workflow = service.create(WorkflowType.KNOWLEDGE_PROCESSING, metadata("Before"));

        Workflow updated = service.updateMetadata(workflow.id(), metadata("After"));
        assertThat(updated.metadata().name()).isEqualTo("After");

        service.start(workflow.id());
        assertThatThrownBy(() -> service.updateMetadata(workflow.id(), metadata("Blocked")))
                .isInstanceOf(WorkflowLifecycleException.class);
    }

    @Test
    void followsDraftToRunningToCompletedLifecycle() {
        PersistentWorkflowService service = service();
        Workflow workflow = service.create(WorkflowType.PUBLICATION, metadata("Publication"));

        assertThat(service.start(workflow.id()).status()).isEqualTo(WorkflowStatus.RUNNING);
        assertThat(service.complete(workflow.id()).status()).isEqualTo(WorkflowStatus.COMPLETED);
    }

    @Test
    void followsRunningToFailedLifecycle() {
        PersistentWorkflowService service = service();
        Workflow workflow = service.create(WorkflowType.RULE_EVALUATION, metadata("Rules"));

        service.start(workflow.id());
        assertThat(service.fail(workflow.id()).status()).isEqualTo(WorkflowStatus.FAILED);
    }

    @Test
    void rejectsInvalidTransitions() {
        PersistentWorkflowService service = service();
        Workflow workflow = service.create(WorkflowType.ASSESSMENT, metadata("Assessment"));

        assertThatThrownBy(() -> service.complete(workflow.id()))
                .isInstanceOf(WorkflowLifecycleException.class);

        service.start(workflow.id());
        service.complete(workflow.id());

        assertThatThrownBy(() -> service.start(workflow.id()))
                .isInstanceOf(WorkflowLifecycleException.class);
    }

    @Test
    void rejectsMissingWorkflow() {
        PersistentWorkflowService service = service();
        var id = de.dn.enterprise.platform.workflow.domain.WorkflowId.newId();
        assertThatThrownBy(() -> service.get(id))
                .isInstanceOf(WorkflowNotFoundException.class);
    }

    @Test
    void exposesRepositoryQueries() {
        PersistentWorkflowService service = service();
        Workflow assessment = service.create(WorkflowType.ASSESSMENT, metadata("A"));
        Workflow publication = service.create(WorkflowType.PUBLICATION, metadata("P"));
        service.start(publication.id());

        assertThat(service.findAll()).containsExactlyInAnyOrder(assessment, publication.withStatus(WorkflowStatus.RUNNING));
        assertThat(service.findByType(WorkflowType.PUBLICATION)).containsExactly(publication.withStatus(WorkflowStatus.RUNNING));
        assertThat(service.findByStatus(WorkflowStatus.RUNNING)).containsExactly(publication.withStatus(WorkflowStatus.RUNNING));
    }

    private static PersistentWorkflowService service() {
        return new PersistentWorkflowService(new InMemoryWorkflowRepository());
    }

    private static WorkflowMetadata metadata(String name) {
        return new WorkflowMetadata(name, "Description", Map.of("source", "test"));
    }
}
