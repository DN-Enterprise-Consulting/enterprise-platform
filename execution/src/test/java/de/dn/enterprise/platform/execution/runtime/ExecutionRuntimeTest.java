package de.dn.enterprise.platform.execution.runtime;

import de.dn.enterprise.platform.execution.domain.ExecutionContext;
import de.dn.enterprise.platform.execution.domain.ExecutionResult;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStep;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStepType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ExecutionRuntimeTest {

    @TempDir
    Path tempDirectory;

    @Test
    void composesAllSupportedExecutionAdapters() {
        ExecutionRuntime runtime = ExecutionRuntime.fileBacked(tempDirectory);

        assertThat(runtime.registry().all())
                .extracting(adapter -> adapter.domainKey())
                .containsExactly("assessment", "knowledge", "publishing", "rules");
    }

    @Test
    void dispatchesAssessmentStepThroughComposedRuntime() {
        ExecutionRuntime runtime = ExecutionRuntime.fileBacked(tempDirectory);

        ExecutionContext context = ExecutionContext.empty()
                .with("assessment.type", "ENTERPRISE_ARCHITECTURE")
                .with("assessment.name", "Runtime Assessment")
                .with("assessment.description", "Runtime composition test");

        ExecutionResult result = runtime.dispatcher().dispatch(
                OrchestrationStep.pending(1, OrchestrationStepType.ASSESSMENT),
                context);

        assertThat(result.successful()).isTrue();
        assertThat(result.outputs()).containsEntry("assessmentType", "ENTERPRISE_ARCHITECTURE");
        assertThat(result.outputs()).containsEntry("assessmentStatus", "COMPLETED");
    }

    @Test
    void dispatchesKnowledgeStepThroughComposedRuntime() {
        ExecutionRuntime runtime = ExecutionRuntime.fileBacked(tempDirectory);

        ExecutionContext context = ExecutionContext.empty()
                .with("knowledge.type", "STANDARD")
                .with("knowledge.name", "Runtime Knowledge")
                .with("knowledge.description", "Runtime composition test")
                .with("knowledge.content", "runtime-content");

        ExecutionResult result = runtime.dispatcher().dispatch(
                OrchestrationStep.pending(1, OrchestrationStepType.KNOWLEDGE_PROCESSING),
                context);

        assertThat(result.successful()).isTrue();
        assertThat(result.outputs()).containsEntry("knowledgeStatus", "ACTIVE");
    }

    @Test
    void dispatchesRuleStepThroughComposedRuntime() {
        ExecutionRuntime runtime = ExecutionRuntime.fileBacked(tempDirectory);

        ExecutionContext context = ExecutionContext.empty()
                .with("rule.type", "COMPLIANCE")
                .with("rule.name", "Runtime Rule")
                .with("rule.description", "Runtime composition test")
                .with("rule.definition", "field != null");

        ExecutionResult result = runtime.dispatcher().dispatch(
                OrchestrationStep.pending(1, OrchestrationStepType.RULE_EVALUATION),
                context);

        assertThat(result.successful()).isTrue();
        assertThat(result.outputs()).containsEntry("ruleStatus", "ACTIVE");
    }

    @Test
    void dispatchesPublicationStepThroughComposedRuntime() {
        ExecutionRuntime runtime = ExecutionRuntime.fileBacked(tempDirectory);

        ExecutionContext context = ExecutionContext.empty()
                .with("publication.type", "ASSESSMENT_REPORT")
                .with("publication.name", "Runtime Publication")
                .with("publication.description", "Runtime composition test");

        ExecutionResult result = runtime.dispatcher().dispatch(
                OrchestrationStep.pending(1, OrchestrationStepType.PUBLICATION),
                context);

        assertThat(result.successful()).isTrue();
        assertThat(result.outputs()).containsEntry("publicationStatus", "PUBLISHED");
    }

    @Test
    void fileBackedRuntimeCanBeRecreatedAgainstSameStorage() {
        ExecutionRuntime first = ExecutionRuntime.fileBacked(tempDirectory);

        ExecutionContext context = ExecutionContext.empty()
                .with("assessment.type", "ENTERPRISE_ARCHITECTURE")
                .with("assessment.name", "Persistent Assessment")
                .with("assessment.description", "Restart test");

        ExecutionResult result = first.dispatcher().dispatch(
                OrchestrationStep.pending(1, OrchestrationStepType.ASSESSMENT),
                context);

        assertThat(result.successful()).isTrue();
        String assessmentId = result.outputs().get("assessmentId");

        ExecutionRuntime second = ExecutionRuntime.fileBacked(tempDirectory);

        assertThat(second.assessmentRuntime().application().get(
                de.dn.enterprise.platform.assessment.domain.AssessmentId.of(
                        java.util.UUID.fromString(assessmentId))))
                .isNotNull();
    }
}
