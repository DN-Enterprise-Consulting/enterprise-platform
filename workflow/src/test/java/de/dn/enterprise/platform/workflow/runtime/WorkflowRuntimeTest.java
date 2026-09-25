package de.dn.enterprise.platform.workflow.runtime;

import de.dn.enterprise.platform.workflow.domain.WorkflowMetadata;
import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class WorkflowRuntimeTest {

    @TempDir
    Path tempDirectory;

    @Test
    void persistsWorkflowAcrossRuntimeRecreation() {
        WorkflowRuntime firstRuntime =
                WorkflowRuntime.fileBacked(tempDirectory);

        var created = firstRuntime.workflowService().create(
                WorkflowType.ASSESSMENT,
                WorkflowMetadata.of("Architecture Assessment"));

        firstRuntime.workflowService().start(created.id());
        firstRuntime.workflowService().complete(created.id());

        WorkflowRuntime recreated =
                WorkflowRuntime.fileBacked(tempDirectory);

        assertThat(recreated.workflowService().get(created.id()).status())
                .isEqualTo(WorkflowStatus.COMPLETED);
        assertThat(recreated.workflowService().findAll())
                .containsExactly(created.withStatus(WorkflowStatus.COMPLETED));
    }

    @Test
    void runtimeUsesFileBackedStorage() {
        WorkflowRuntime runtime =
                WorkflowRuntime.fileBacked(tempDirectory);

        var created = runtime.workflowService().create(
                WorkflowType.PUBLICATION,
                WorkflowMetadata.of("Publication Workflow"));

        assertThat(tempDirectory.resolve(
                created.id().value() + ".workflow")).exists();
    }

    @Test
    void rejectsNullStorageDirectory() {
        org.assertj.core.api.Assertions.assertThatThrownBy(
                () -> WorkflowRuntime.fileBacked(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("storageDirectory must not be null");
    }
}
