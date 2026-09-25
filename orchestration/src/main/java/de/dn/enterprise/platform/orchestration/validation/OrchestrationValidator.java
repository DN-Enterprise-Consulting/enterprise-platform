package de.dn.enterprise.platform.orchestration.validation;

import de.dn.enterprise.platform.orchestration.domain.Orchestration;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationMetadata;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStep;

import java.util.Map;
import java.util.Objects;

public final class OrchestrationValidator {

    public void validate(Orchestration orchestration) {
        Objects.requireNonNull(orchestration, "orchestration must not be null");
        if (orchestration.id() == null) throw new OrchestrationValidationException("orchestration id must not be null");
        if (orchestration.type() == null) throw new OrchestrationValidationException("orchestration type must not be null");
        if (orchestration.status() == null) throw new OrchestrationValidationException("orchestration status must not be null");
        validateMetadata(orchestration.metadata());
        if (orchestration.steps() == null || orchestration.steps().isEmpty())
            throw new OrchestrationValidationException("orchestration steps must not be empty");
        for (int i = 0; i < orchestration.steps().size(); i++) {
            OrchestrationStep step = orchestration.steps().get(i);
            if (step == null) throw new OrchestrationValidationException("orchestration step must not be null");
            if (step.sequence() != i + 1)
                throw new OrchestrationValidationException("orchestration steps must have contiguous sequence numbers starting at 1");
            if (step.type() == null) throw new OrchestrationValidationException("orchestration step type must not be null");
            if (step.status() == null) throw new OrchestrationValidationException("orchestration step status must not be null");
        }
    }

    public void validateMetadata(OrchestrationMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata must not be null");
        if (metadata.name() == null || metadata.name().isBlank())
            throw new OrchestrationValidationException("orchestration metadata name must not be blank");
        if (metadata.description() == null)
            throw new OrchestrationValidationException("orchestration metadata description must not be null");
        if (metadata.attributes() == null)
            throw new OrchestrationValidationException("orchestration metadata attributes must not be null");
        for (Map.Entry<String, String> entry : metadata.attributes().entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank())
                throw new OrchestrationValidationException("orchestration metadata attribute key must not be blank");
            if (entry.getValue() == null)
                throw new OrchestrationValidationException("orchestration metadata attribute value must not be null");
        }
    }
}
