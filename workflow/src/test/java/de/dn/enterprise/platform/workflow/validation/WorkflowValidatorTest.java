package de.dn.enterprise.platform.workflow.validation;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.domain.WorkflowMetadata;
import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkflowValidatorTest {

    private static WorkflowMetadata metadata() {
        return new WorkflowMetadata("workflow", "description", Map.of("owner", "test"));
    }

    @Test
    void acceptsValidWorkflow() {
        Workflow workflow = Workflow.draft(WorkflowType.ASSESSMENT, metadata());
        assertThatCode(() -> new WorkflowValidator().validate(workflow)).doesNotThrowAnyException();
    }

    @Test
    void rejectsNullWorkflow() {
        assertThatThrownBy(() -> new WorkflowValidator().validate(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("workflow must not be null");
    }

    @Test
    void rejectsNullMetadata() {
        assertThatThrownBy(() -> new WorkflowValidator().validateMetadata(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("metadata must not be null");
    }

    @Test
    void rejectsBlankMetadataName() {
        assertThatThrownBy(() -> new WorkflowValidator().validateMetadata(
                new WorkflowMetadata(" ", "description", Map.of())))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void acceptsTerminalStatuses() {
        Workflow workflow = Workflow.draft(WorkflowType.PUBLICATION, metadata())
                .withStatus(WorkflowStatus.RUNNING)
                .withStatus(WorkflowStatus.COMPLETED);
        assertThatCode(() -> new WorkflowValidator().validate(workflow)).doesNotThrowAnyException();
    }
}
