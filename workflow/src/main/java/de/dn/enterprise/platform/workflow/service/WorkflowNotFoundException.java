package de.dn.enterprise.platform.workflow.service;

import de.dn.enterprise.platform.workflow.domain.WorkflowId;

public final class WorkflowNotFoundException extends RuntimeException {
    public WorkflowNotFoundException(WorkflowId id) {
        super("Workflow not found: " + id.value());
    }
}
