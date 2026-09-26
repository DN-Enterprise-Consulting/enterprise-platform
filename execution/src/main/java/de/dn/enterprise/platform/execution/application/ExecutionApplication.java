package de.dn.enterprise.platform.execution.application;

import de.dn.enterprise.platform.execution.domain.ExecutionContext;
import de.dn.enterprise.platform.execution.domain.ExecutionResult;
import de.dn.enterprise.platform.execution.runtime.ExecutionRuntime;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStep;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStepType;

import java.util.Objects;

/** Stable application boundary for execution use cases. */
public final class ExecutionApplication {

    private final ExecutionRuntime runtime;

    public ExecutionApplication(ExecutionRuntime runtime) {
        this.runtime = Objects.requireNonNull(runtime, "runtime must not be null");
    }

    /** Executes one orchestration step through the composed execution runtime. */
    public ExecutionResult execute(OrchestrationStep step, ExecutionContext context) {
        return runtime.dispatcher().dispatch(step, context);
    }

    /** Resolves the execution domain key for an orchestration step type. */
    public String domainKeyFor(OrchestrationStepType stepType) {
        return runtime.dispatcher().domainKeyFor(stepType);
    }
}
