package de.dn.enterprise.platform.workflow.query;

import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;

import java.util.Optional;

public record WorkflowQuery(
        Optional<WorkflowType> type,
        Optional<WorkflowStatus> status) {

    public WorkflowQuery {
        type = type == null ? Optional.empty() : type;
        status = status == null ? Optional.empty() : status;
    }

    public static WorkflowQuery all() {
        return new WorkflowQuery(Optional.empty(), Optional.empty());
    }

    public static WorkflowQuery byType(WorkflowType type) {
        return new WorkflowQuery(Optional.of(type), Optional.empty());
    }

    public static WorkflowQuery byStatus(WorkflowStatus status) {
        return new WorkflowQuery(Optional.empty(), Optional.of(status));
    }

    public static WorkflowQuery byTypeAndStatus(WorkflowType type, WorkflowStatus status) {
        return new WorkflowQuery(Optional.of(type), Optional.of(status));
    }
}
