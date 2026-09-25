package de.dn.enterprise.platform.workflow.service;

import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;

public final class WorkflowLifecycleException extends RuntimeException {
    public WorkflowLifecycleException(WorkflowStatus current, WorkflowStatus target) {
        super("Invalid workflow lifecycle transition: " + current + " -> " + target);
    }
}
