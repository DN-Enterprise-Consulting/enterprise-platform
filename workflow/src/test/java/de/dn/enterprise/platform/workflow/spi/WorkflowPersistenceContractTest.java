package de.dn.enterprise.platform.workflow.spi;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.domain.WorkflowId;
import de.dn.enterprise.platform.workflow.domain.WorkflowMetadata;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class WorkflowPersistenceContractTest {

    @Test
    void contractCanBeImplementedWithoutPersistenceTechnology() {
        InMemoryPersistenceStub persistence = new InMemoryPersistenceStub();

        Workflow workflow = Workflow.draft(
                WorkflowType.ASSESSMENT,
                WorkflowMetadata.of("Test"));

        Workflow saved = persistence.save(workflow);

        assertThat(saved).isSameAs(workflow);
        assertThat(persistence.load(workflow.id())).containsSame(workflow);
        assertThat(persistence.exists(workflow.id())).isTrue();

        persistence.delete(workflow.id());

        assertThat(persistence.load(workflow.id())).isEmpty();
        assertThat(persistence.exists(workflow.id())).isFalse();
    }

    private static final class InMemoryPersistenceStub
            implements WorkflowPersistence {

        private final Map<WorkflowId, Workflow> workflows = new HashMap<>();

        @Override
        public Workflow save(Workflow workflow) {
            workflows.put(workflow.id(), workflow);
            return workflow;
        }

        @Override
        public Optional<Workflow> load(WorkflowId id) {
            return Optional.ofNullable(workflows.get(id));
        }

        @Override
        public boolean exists(WorkflowId id) {
            return workflows.containsKey(id);
        }

        @Override
        public void delete(WorkflowId id) {
            workflows.remove(id);
        }
    }
}
