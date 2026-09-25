package de.dn.enterprise.platform.workflow.service;

import de.dn.enterprise.platform.workflow.domain.WorkflowMetadata;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;
import de.dn.enterprise.platform.workflow.inmemory.InMemoryWorkflowRepository;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersistentWorkflowServiceHardeningTest {

    private static WorkflowMetadata metadata(String name) {
        return new WorkflowMetadata(name, "description", Map.of("owner", "test"));
    }

    @Test
    void rejectsNullRepository() {
        assertThatThrownBy(() -> new PersistentWorkflowService(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("repository must not be null");
    }

    @Test
    void rejectsNullCreateArguments() {
        PersistentWorkflowService service = new PersistentWorkflowService(new InMemoryWorkflowRepository());
        assertThatThrownBy(() -> service.create(null, metadata("workflow")))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("type must not be null");
        assertThatThrownBy(() -> service.create(WorkflowType.ASSESSMENT, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("metadata must not be null");
    }

    @Test
    void rejectsMetadataChangesAfterStart() {
        PersistentWorkflowService service = new PersistentWorkflowService(new InMemoryWorkflowRepository());
        var workflow = service.create(WorkflowType.ASSESSMENT, metadata("workflow"));
        service.start(workflow.id());

        assertThatThrownBy(() -> service.updateMetadata(workflow.id(), metadata("changed")))
                .isInstanceOf(WorkflowLifecycleException.class)
                .hasMessage("Invalid workflow lifecycle transition: RUNNING -> RUNNING");
    }

    @Test
    void terminalWorkflowCannotBeCompletedAgain() {
        PersistentWorkflowService service = new PersistentWorkflowService(new InMemoryWorkflowRepository());
        var workflow = service.create(WorkflowType.ASSESSMENT, metadata("workflow"));
        service.start(workflow.id());
        service.complete(workflow.id());

        assertThatThrownBy(() -> service.complete(workflow.id()))
                .isInstanceOf(WorkflowLifecycleException.class)
                .hasMessage("Invalid workflow lifecycle transition: COMPLETED -> COMPLETED");
    }
}
