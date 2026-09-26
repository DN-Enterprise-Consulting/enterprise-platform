package de.dn.enterprise.platform.execution.application;

import de.dn.enterprise.platform.execution.domain.ExecutionContext;
import de.dn.enterprise.platform.execution.domain.ExecutionResult;
import de.dn.enterprise.platform.execution.runtime.ExecutionRuntime;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStep;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStepType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ExecutionApplicationTest {

    @TempDir
    Path tempDirectory;

    @Test
    void executesAssessmentThroughStableApplicationBoundary() {
        ExecutionApplication application = ExecutionRuntime.fileBacked(tempDirectory).application();

        ExecutionContext context = ExecutionContext.empty()
                .with("assessment.type", "ENTERPRISE_ARCHITECTURE")
                .with("assessment.name", "Application Assessment")
                .with("assessment.description", "Application boundary test");

        ExecutionResult result = application.execute(
                OrchestrationStep.pending(1, OrchestrationStepType.ASSESSMENT),
                context);

        assertThat(result.successful()).isTrue();
        assertThat(result.outputs()).containsEntry("assessmentStatus", "COMPLETED");
    }

    @Test
    void resolvesDomainKeyThroughStableApplicationBoundary() {
        ExecutionApplication application = ExecutionRuntime.fileBacked(tempDirectory).application();

        assertThat(application.domainKeyFor(OrchestrationStepType.KNOWLEDGE_PROCESSING))
                .isEqualTo("knowledge");
        assertThat(application.domainKeyFor(OrchestrationStepType.RULE_EVALUATION))
                .isEqualTo("rules");
        assertThat(application.domainKeyFor(OrchestrationStepType.PUBLICATION))
                .isEqualTo("publishing");
    }
}
