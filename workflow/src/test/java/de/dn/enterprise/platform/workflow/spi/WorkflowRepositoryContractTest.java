package de.dn.enterprise.platform.workflow.spi;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.domain.WorkflowId;
import de.dn.enterprise.platform.workflow.domain.WorkflowMetadata;
import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class WorkflowRepositoryContractTest {

    @Test
    void contractCanBeImplementedWithoutInfrastructureDependencies() {
        WorkflowRepository repository = new InMemoryWorkflowRepositoryStub();
        Workflow workflow = Workflow.draft(
                WorkflowType.values()[0],
                new WorkflowMetadata("Assessment workflow", "", Map.of()));

        assertThat(repository.save(workflow)).isEqualTo(workflow);
        assertThat(repository.findById(workflow.id())).contains(workflow);
        assertThat(repository.findAll()).containsExactly(workflow);
        assertThat(repository.findByType(WorkflowType.values()[0])).containsExactly(workflow);
        assertThat(repository.findByStatus(WorkflowStatus.DRAFT)).containsExactly(workflow);
        assertThat(repository.existsById(workflow.id())).isTrue();
    }

    private static final class InMemoryWorkflowRepositoryStub implements WorkflowRepository {
        private final Map<WorkflowId, Workflow> workflows = new HashMap<>();

        @Override
        public Workflow save(Workflow workflow) {
            workflows.put(workflow.id(), workflow);
            return workflow;
        }

        @Override
        public Optional<Workflow> findById(WorkflowId id) {
            return Optional.ofNullable(workflows.get(id));
        }

        @Override
        public List<Workflow> findAll() {
            return List.copyOf(workflows.values());
        }

        @Override
        public List<Workflow> findByType(WorkflowType type) {
            return workflows.values().stream()
                    .filter(workflow -> workflow.type() == type)
                    .toList();
        }

        @Override
        public List<Workflow> findByStatus(WorkflowStatus status) {
            return workflows.values().stream()
                    .filter(workflow -> workflow.status() == status)
                    .toList();
        }

        @Override
        public boolean existsById(WorkflowId id) {
            return workflows.containsKey(id);
        }
    }
}
