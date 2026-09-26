package de.dn.enterprise.platform.execution.dispatcher;

import de.dn.enterprise.platform.execution.domain.ExecutionContext;
import de.dn.enterprise.platform.execution.domain.ExecutionResult;
import de.dn.enterprise.platform.execution.spi.ExecutionAdapter;
import de.dn.enterprise.platform.execution.spi.ExecutionAdapterRegistry;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStep;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStepType;

import java.util.Map;
import java.util.Objects;

/**
 * Dispatches one orchestration step to the execution adapter responsible for
 * the corresponding business domain.
 */
public final class ExecutionStepDispatcher {

    private static final Map<OrchestrationStepType, String> DOMAIN_KEYS = Map.of(
            OrchestrationStepType.ASSESSMENT, "assessment",
            OrchestrationStepType.RULE_EVALUATION, "rules",
            OrchestrationStepType.KNOWLEDGE_PROCESSING, "knowledge",
            OrchestrationStepType.PUBLICATION, "publishing");

    private final ExecutionAdapterRegistry registry;

    public ExecutionStepDispatcher(ExecutionAdapterRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry must not be null");
    }

    public ExecutionResult dispatch(OrchestrationStep step, ExecutionContext context) {
        Objects.requireNonNull(step, "step must not be null");
        Objects.requireNonNull(context, "context must not be null");

        String domainKey = DOMAIN_KEYS.get(step.type());
        if (domainKey == null) {
            throw new ExecutionStepDispatchException(
                    "No execution domain mapping for orchestration step type: " + step.type());
        }

        ExecutionAdapter adapter = registry.requireByDomainKey(domainKey);
        return adapter.execute(context);
    }

    public String domainKeyFor(OrchestrationStepType stepType) {
        Objects.requireNonNull(stepType, "stepType must not be null");
        String domainKey = DOMAIN_KEYS.get(stepType);
        if (domainKey == null) {
            throw new ExecutionStepDispatchException(
                    "No execution domain mapping for orchestration step type: " + stepType);
        }
        return domainKey;
    }
}
