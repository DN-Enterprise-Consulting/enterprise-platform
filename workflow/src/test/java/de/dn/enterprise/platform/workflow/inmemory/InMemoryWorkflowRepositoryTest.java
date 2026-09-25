package de.dn.enterprise.platform.workflow.inmemory;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.domain.WorkflowMetadata;
import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryWorkflowRepositoryTest {

    private final InMemoryWorkflowRepository repository = new InMemoryWorkflowRepository();

    @Test
    void savesAndFindsWorkflowById() {
        Workflow workflow = workflow(WorkflowType.ASSESSMENT, WorkflowStatus.DRAFT);

        assertThat(repository.save(workflow)).isEqualTo(workflow);
        assertThat(repository.findById(workflow.id())).contains(workflow);
    }

    @Test
    void overwritesWorkflowWithSameId() {
        Workflow draft = workflow(WorkflowType.ASSESSMENT, WorkflowStatus.DRAFT);
        Workflow running = draft.withStatus(WorkflowStatus.RUNNING);

        repository.save(draft);
        repository.save(running);

        assertThat(repository.findById(draft.id())).contains(running);
        assertThat(repository.findAll()).containsExactly(running);
    }

    @Test
    void returnsAllWorkflows() {
        Workflow assessment = workflow(WorkflowType.ASSESSMENT, WorkflowStatus.DRAFT);
        Workflow publication = workflow(WorkflowType.PUBLICATION, WorkflowStatus.COMPLETED);

        repository.save(assessment);
        repository.save(publication);

        assertThat(repository.findAll()).containsExactlyInAnyOrder(assessment, publication);
    }

    @Test
    void filtersByTypeAndStatus() {
        Workflow assessmentDraft = workflow(WorkflowType.ASSESSMENT, WorkflowStatus.DRAFT);
        Workflow assessmentRunning = workflow(WorkflowType.ASSESSMENT, WorkflowStatus.RUNNING);
        Workflow publicationDraft = workflow(WorkflowType.PUBLICATION, WorkflowStatus.DRAFT);

        repository.save(assessmentDraft);
        repository.save(assessmentRunning);
        repository.save(publicationDraft);

        assertThat(repository.findByType(WorkflowType.ASSESSMENT))
                .containsExactlyInAnyOrder(assessmentDraft, assessmentRunning);
        assertThat(repository.findByStatus(WorkflowStatus.DRAFT))
                .containsExactlyInAnyOrder(assessmentDraft, publicationDraft);
    }

    @Test
    void reportsExistenceAndAbsence() {
        Workflow workflow = workflow(WorkflowType.KNOWLEDGE_PROCESSING, WorkflowStatus.DRAFT);

        assertThat(repository.existsById(workflow.id())).isFalse();
        repository.save(workflow);
        assertThat(repository.existsById(workflow.id())).isTrue();
    }

    @Test
    void rejectsNullArguments() {
        Workflow workflow = workflow(WorkflowType.RULE_EVALUATION, WorkflowStatus.DRAFT);

        assertThatThrownBy(() -> repository.save(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findById(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findByType(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findByStatus(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.existsById(null))
                .isInstanceOf(NullPointerException.class);

        assertThat(repository.findById(workflow.id())).isEmpty();
    }

    private static Workflow workflow(WorkflowType type, WorkflowStatus status) {
        Workflow workflow = Workflow.draft(
                type,
                new WorkflowMetadata("Workflow", "", Map.of()));
        return workflow.withStatus(status);
    }
}
