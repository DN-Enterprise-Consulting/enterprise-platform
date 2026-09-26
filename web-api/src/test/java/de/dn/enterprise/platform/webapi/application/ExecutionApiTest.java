package de.dn.enterprise.platform.webapi.application;

import de.dn.enterprise.platform.execution.runtime.ExecutionRuntime;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStepType;
import de.dn.enterprise.platform.webapi.model.ExecutionRequest;
import de.dn.enterprise.platform.webapi.model.ExecutionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ExecutionApiTest {

    @TempDir
    Path tempDirectory;

    @Test
    void executesStepThroughStableApiBoundary() {
        ExecutionApi api = new ExecutionApi(ExecutionRuntime.fileBacked(tempDirectory).application());

        ExecutionResponse response = api.execute(new ExecutionRequest(
                1,
                OrchestrationStepType.ASSESSMENT,
                Map.of(
                        "assessment.type", "ENTERPRISE_ARCHITECTURE",
                        "assessment.name", "API Assessment",
                        "assessment.description", "M9.11 API boundary test")));

        assertThat(response.successful()).isTrue();
        assertThat(response.status()).isEqualTo(de.dn.enterprise.platform.execution.domain.ExecutionResultStatus.SUCCESS);
        assertThat(response.outputs()).containsEntry("assessmentStatus", "COMPLETED");
    }
}
