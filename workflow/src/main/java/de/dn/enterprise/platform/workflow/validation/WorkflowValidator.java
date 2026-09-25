package de.dn.enterprise.platform.workflow.validation;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.domain.WorkflowMetadata;

import java.util.Map;
import java.util.Objects;

public final class WorkflowValidator {

    public void validate(Workflow workflow) {
        Objects.requireNonNull(workflow, "workflow must not be null");

        if (workflow.id() == null) {
            throw new WorkflowValidationException("workflow id must not be null");
        }
        if (workflow.type() == null) {
            throw new WorkflowValidationException("workflow type must not be null");
        }
        if (workflow.status() == null) {
            throw new WorkflowValidationException("workflow status must not be null");
        }

        validateMetadata(workflow.metadata());
    }

    public void validateMetadata(WorkflowMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata must not be null");

        if (metadata.name() == null || metadata.name().isBlank()) {
            throw new WorkflowValidationException("workflow metadata name must not be blank");
        }
        if (metadata.description() == null) {
            throw new WorkflowValidationException("workflow metadata description must not be null");
        }
        if (metadata.attributes() == null) {
            throw new WorkflowValidationException("workflow metadata attributes must not be null");
        }

        for (Map.Entry<String, String> entry : metadata.attributes().entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank()) {
                throw new WorkflowValidationException("workflow metadata attribute key must not be blank");
            }
            if (entry.getValue() == null) {
                throw new WorkflowValidationException("workflow metadata attribute value must not be null");
            }
        }
    }
}
