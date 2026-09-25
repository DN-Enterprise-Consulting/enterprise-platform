package de.dn.enterprise.platform.orchestration.runtime;

import de.dn.enterprise.platform.orchestration.domain.OrchestrationMetadata;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStatus;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStep;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStepStatus;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStepType;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrchestrationRuntimeTest {

    @TempDir
    Path tempDirectory;

    @Test
    void persistsOrchestrationAcrossRuntimeRecreation() {
        OrchestrationRuntime firstRuntime =
                OrchestrationRuntime.fileBacked(tempDirectory);

        var created = firstRuntime.orchestrationService().create(
                OrchestrationType.FULL_ASSESSMENT,
                new OrchestrationMetadata("Full Assessment", "Full Assessment", java.util.Map.of()),
                List.of(new OrchestrationStep(1, OrchestrationStepType.ASSESSMENT, OrchestrationStepStatus.PENDING)));

        firstRuntime.orchestrationService().start(created.id());
        firstRuntime.orchestrationService().complete(created.id());

        OrchestrationRuntime recreated =
                OrchestrationRuntime.fileBacked(tempDirectory);

        assertThat(recreated.orchestrationService().get(created.id()).status())
                .isEqualTo(OrchestrationStatus.COMPLETED);
        assertThat(recreated.orchestrationService().findAll())
                .hasSize(1)
                .first()
                .extracting(o -> o.id())
                .isEqualTo(created.id());
    }

    @Test
    void runtimeUsesFileBackedStorage() {
        OrchestrationRuntime runtime =
                OrchestrationRuntime.fileBacked(tempDirectory);

        var created = runtime.orchestrationService().create(
                OrchestrationType.ASSESSMENT_TO_KNOWLEDGE,
                new OrchestrationMetadata("Assessment to Knowledge", "Assessment to Knowledge", java.util.Map.of()),
                List.of(new OrchestrationStep(1, OrchestrationStepType.ASSESSMENT, OrchestrationStepStatus.PENDING)));

        assertThat(tempDirectory.resolve(
                created.id().value() + ".orchestration")).exists();
    }

    @Test
    void rejectsNullStorageDirectory() {
        assertThatThrownBy(() -> OrchestrationRuntime.fileBacked(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("storageDirectory must not be null");
    }
}
