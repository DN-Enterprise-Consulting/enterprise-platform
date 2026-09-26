package de.dn.enterprise.platform.execution.dispatcher;

import de.dn.enterprise.platform.execution.domain.ExecutionContext;
import de.dn.enterprise.platform.execution.domain.ExecutionResult;
import de.dn.enterprise.platform.execution.registry.InMemoryExecutionAdapterRegistry;
import de.dn.enterprise.platform.execution.spi.ExecutionAdapter;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStep;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStepType;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExecutionStepDispatcherTest {

    @Test
    void dispatchesAssessmentStepToAssessmentAdapter() {
        var registry = new InMemoryExecutionAdapterRegistry();
        var received = new AtomicReference<ExecutionContext>();
        registry.register(adapter("assessment", received));

        var dispatcher = new ExecutionStepDispatcher(registry);
        var context = ExecutionContext.empty().with("assessment.type", "ENTERPRISE_ARCHITECTURE");

        ExecutionResult result = dispatcher.dispatch(
                OrchestrationStep.pending(1, OrchestrationStepType.ASSESSMENT), context);

        assertThat(result.successful()).isTrue();
        assertThat(received.get()).isSameAs(context);
        assertThat(dispatcher.domainKeyFor(OrchestrationStepType.ASSESSMENT)).isEqualTo("assessment");
    }

    @Test
    void mapsAllSupportedStepTypes() {
        var registry = new InMemoryExecutionAdapterRegistry();
        registry.register(adapter("assessment", new AtomicReference<>()));
        registry.register(adapter("rules", new AtomicReference<>()));
        registry.register(adapter("knowledge", new AtomicReference<>()));
        registry.register(adapter("publishing", new AtomicReference<>()));

        var dispatcher = new ExecutionStepDispatcher(registry);

        assertThat(dispatcher.domainKeyFor(OrchestrationStepType.ASSESSMENT)).isEqualTo("assessment");
        assertThat(dispatcher.domainKeyFor(OrchestrationStepType.RULE_EVALUATION)).isEqualTo("rules");
        assertThat(dispatcher.domainKeyFor(OrchestrationStepType.KNOWLEDGE_PROCESSING)).isEqualTo("knowledge");
        assertThat(dispatcher.domainKeyFor(OrchestrationStepType.PUBLICATION)).isEqualTo("publishing");
    }

    @Test
    void failsWhenAdapterIsNotRegistered() {
        var dispatcher = new ExecutionStepDispatcher(new InMemoryExecutionAdapterRegistry());

        assertThatThrownBy(() -> dispatcher.dispatch(
                OrchestrationStep.pending(1, OrchestrationStepType.PUBLICATION),
                ExecutionContext.empty()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("publishing");
    }

    private ExecutionAdapter adapter(String domainKey, AtomicReference<ExecutionContext> received) {
        return new ExecutionAdapter() {
            @Override
            public String domainKey() {
                return domainKey;
            }

            @Override
            public ExecutionResult execute(ExecutionContext context) {
                received.set(context);
                return ExecutionResult.success("dispatched");
            }
        };
    }
}
