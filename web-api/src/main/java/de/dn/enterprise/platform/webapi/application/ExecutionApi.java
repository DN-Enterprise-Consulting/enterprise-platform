package de.dn.enterprise.platform.webapi.application;

import de.dn.enterprise.platform.execution.application.ExecutionApplication;
import de.dn.enterprise.platform.execution.domain.ExecutionContext;
import de.dn.enterprise.platform.execution.domain.ExecutionResult;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStep;
import de.dn.enterprise.platform.webapi.model.ExecutionRequest;
import de.dn.enterprise.platform.webapi.model.ExecutionResponse;

import java.util.Objects;

/**
 * Stable API boundary for execution use cases.
 *
 * <p>The API translates transport-facing request/response models to the
 * application boundary and deliberately does not expose domain internals.</p>
 */
public final class ExecutionApi {

    private final ExecutionApplication application;

    public ExecutionApi(ExecutionApplication application) {
        this.application = Objects.requireNonNull(application, "application must not be null");
    }

    public ExecutionResponse execute(ExecutionRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        ExecutionContext context = new ExecutionContext(request.context());
        ExecutionResult result = application.execute(
                OrchestrationStep.pending(request.sequence(), request.stepType()),
                context);

        return new ExecutionResponse(
                result.status(),
                result.successful(),
                result.summary(),
                result.outputs());
    }
}
