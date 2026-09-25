package de.dn.enterprise.platform.workflow.query;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.domain.WorkflowMetadata;
import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;
import de.dn.enterprise.platform.workflow.inmemory.InMemoryWorkflowRepository;
import de.dn.enterprise.platform.workflow.spi.WorkflowRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkflowQueryServiceTest {

    private static WorkflowMetadata metadata(String name) {
        return new WorkflowMetadata(name, name + " description", Map.of("owner", "test"));
    }

    private static Workflow workflow(WorkflowType type, WorkflowStatus status, String name) {
        Workflow workflow = Workflow.draft(type, metadata(name));
        if (status == WorkflowStatus.RUNNING) {
            return workflow.withStatus(WorkflowStatus.RUNNING);
        }
        if (status == WorkflowStatus.COMPLETED) {
            return workflow.withStatus(WorkflowStatus.RUNNING).withStatus(WorkflowStatus.COMPLETED);
        }
        if (status == WorkflowStatus.FAILED) {
            return workflow.withStatus(WorkflowStatus.RUNNING).withStatus(WorkflowStatus.FAILED);
        }
        return workflow;
    }

    private static WorkflowQueryService serviceWithSampleData() {
        WorkflowRepository repository = new InMemoryWorkflowRepository();
        repository.save(workflow(WorkflowType.ASSESSMENT, WorkflowStatus.DRAFT, "assessment-draft"));
        repository.save(workflow(WorkflowType.ASSESSMENT, WorkflowStatus.COMPLETED, "assessment-completed"));
        repository.save(workflow(WorkflowType.PUBLICATION, WorkflowStatus.RUNNING, "publication-running"));
        repository.save(workflow(WorkflowType.PUBLICATION, WorkflowStatus.FAILED, "publication-failed"));
        return new WorkflowQueryService(repository);
    }

    @Test
    void allReturnsAllWorkflows() {
        assertThat(serviceWithSampleData().query(WorkflowQuery.all())).hasSize(4);
    }

    @Test
    void filtersByType() {
        List<Workflow> result = serviceWithSampleData().query(WorkflowQuery.byType(WorkflowType.PUBLICATION));
        assertThat(result).hasSize(2).allMatch(workflow -> workflow.type() == WorkflowType.PUBLICATION);
    }

    @Test
    void filtersByStatus() {
        List<Workflow> result = serviceWithSampleData().query(WorkflowQuery.byStatus(WorkflowStatus.COMPLETED));
        assertThat(result).hasSize(1).allMatch(workflow -> workflow.status() == WorkflowStatus.COMPLETED);
    }

    @Test
    void combinesTypeAndStatusWithAndSemantics() {
        List<Workflow> result = serviceWithSampleData().query(
                WorkflowQuery.byTypeAndStatus(WorkflowType.PUBLICATION, WorkflowStatus.FAILED));
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().type()).isEqualTo(WorkflowType.PUBLICATION);
        assertThat(result.getFirst().status()).isEqualTo(WorkflowStatus.FAILED);
    }

    @Test
    void returnsEmptyWhenNothingMatches() {
        assertThat(serviceWithSampleData().query(
                WorkflowQuery.byTypeAndStatus(WorkflowType.KNOWLEDGE_PROCESSING, WorkflowStatus.COMPLETED)))
                .isEmpty();
    }

    @Test
    void rejectsNullQuery() {
        assertThatThrownBy(() -> serviceWithSampleData().query(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("query must not be null");
    }
}
