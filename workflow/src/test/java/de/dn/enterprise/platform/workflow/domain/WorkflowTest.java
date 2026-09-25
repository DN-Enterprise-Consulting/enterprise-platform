package de.dn.enterprise.platform.workflow.domain;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkflowTest {

    @Test
    void createsDraftWorkflow() {
        Workflow workflow = Workflow.draft(
                WorkflowType.ASSESSMENT,
                WorkflowMetadata.of("Assessment workflow"));

        assertThat(workflow.id()).isNotNull();
        assertThat(workflow.type()).isEqualTo(WorkflowType.ASSESSMENT);
        assertThat(workflow.status()).isEqualTo(WorkflowStatus.DRAFT);
        assertThat(workflow.metadata().name())
                .isEqualTo("Assessment workflow");
    }

    @Test
    void createsAndParsesWorkflowId() {
        UUID uuid = UUID.randomUUID();

        WorkflowId id = WorkflowId.of(uuid);

        assertThat(id.value()).isEqualTo(uuid);
        assertThat(WorkflowId.parse(uuid.toString())).isEqualTo(id);
    }

    @Test
    void changesStatusAndMetadataImmutably() {
        Workflow workflow = Workflow.draft(
                WorkflowType.PUBLICATION,
                WorkflowMetadata.of("Publication"));

        Workflow running = workflow.withStatus(WorkflowStatus.RUNNING);
        Workflow renamed = running.withMetadata(
                new WorkflowMetadata(
                        "Publication v2",
                        "Description",
                        Map.of("owner", "platform")));

        assertThat(workflow.status()).isEqualTo(WorkflowStatus.DRAFT);
        assertThat(running.status()).isEqualTo(WorkflowStatus.RUNNING);
        assertThat(renamed.metadata().name()).isEqualTo("Publication v2");
        assertThat(renamed.metadata().attributes())
                .containsEntry("owner", "platform");
    }

    @Test
    void rejectsInvalidDomainValues() {
        assertThatThrownBy(() -> new WorkflowId(null))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> WorkflowMetadata.of(" "))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Workflow(
                WorkflowId.newId(),
                null,
                WorkflowStatus.DRAFT,
                WorkflowMetadata.of("Workflow")))
                .isInstanceOf(NullPointerException.class);
    }
}
