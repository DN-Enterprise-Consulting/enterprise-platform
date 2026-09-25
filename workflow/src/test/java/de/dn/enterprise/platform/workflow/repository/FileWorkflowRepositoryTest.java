package de.dn.enterprise.platform.workflow.repository;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.domain.WorkflowMetadata;
import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;
import de.dn.enterprise.platform.workflow.persistence.FileWorkflowPersistence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileWorkflowRepositoryTest {

    @TempDir
    Path tempDirectory;

    @Test
    void savesAndFindsWorkflowById() {
        FileWorkflowRepository repository =
                repository();
        Workflow workflow = workflow(WorkflowType.ASSESSMENT, WorkflowStatus.DRAFT);

        assertThat(repository.save(workflow)).isSameAs(workflow);
        assertThat(repository.findById(workflow.id())).contains(workflow);
        assertThat(repository.existsById(workflow.id())).isTrue();
    }

    @Test
    void findsAllPersistedWorkflowsAfterRepositoryRecreation() {
        FileWorkflowRepository first = repository();
        Workflow assessment = workflow(WorkflowType.ASSESSMENT, WorkflowStatus.DRAFT);
        Workflow publication = workflow(WorkflowType.PUBLICATION, WorkflowStatus.RUNNING);

        first.save(assessment);
        first.save(publication);

        FileWorkflowRepository second = repository();

        assertThat(second.findAll())
                .containsExactlyInAnyOrder(assessment, publication);
    }

    @Test
    void filtersByTypeAndStatus() {
        FileWorkflowRepository repository = repository();
        Workflow assessment = workflow(WorkflowType.ASSESSMENT, WorkflowStatus.DRAFT);
        Workflow rule = workflow(WorkflowType.RULE_EVALUATION, WorkflowStatus.RUNNING);
        Workflow publication = workflow(WorkflowType.PUBLICATION, WorkflowStatus.RUNNING);

        repository.save(assessment);
        repository.save(rule);
        repository.save(publication);

        assertThat(repository.findByType(WorkflowType.RULE_EVALUATION))
                .containsExactly(rule);
        assertThat(repository.findByStatus(WorkflowStatus.RUNNING))
                .containsExactlyInAnyOrder(rule, publication);
    }

    @Test
    void emptyStorageReturnsEmptyList() {
        assertThat(repository().findAll()).isEmpty();
    }

    @Test
    void rejectsNullArguments() {
        FileWorkflowRepository repository = repository();

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
    }

    private FileWorkflowRepository repository() {
        return new FileWorkflowRepository(
                new FileWorkflowPersistence(tempDirectory));
    }

    private static Workflow workflow(WorkflowType type, WorkflowStatus status) {
        return Workflow.draft(
                type,
                new WorkflowMetadata(
                        type.name(),
                        "Workflow",
                        Map.of("type", type.name())))
                .withStatus(status);
    }
}
