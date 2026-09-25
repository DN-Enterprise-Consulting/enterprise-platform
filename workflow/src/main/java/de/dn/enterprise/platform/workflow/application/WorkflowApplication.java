package de.dn.enterprise.platform.workflow.application;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.domain.WorkflowId;
import de.dn.enterprise.platform.workflow.domain.WorkflowMetadata;
import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;
import de.dn.enterprise.platform.workflow.service.PersistentWorkflowService;

import java.util.List;
import java.util.Objects;

/** Stable application boundary for workflow use cases. */
public final class WorkflowApplication {

    private final PersistentWorkflowService service;

    public WorkflowApplication(PersistentWorkflowService service) {
        this.service = Objects.requireNonNull(service, "service must not be null");
    }

    public Workflow create(WorkflowType type, WorkflowMetadata metadata) {
        return service.create(type, metadata);
    }

    public Workflow get(WorkflowId id) {
        return service.get(id);
    }

    public List<Workflow> findAll() {
        return service.findAll();
    }

    public List<Workflow> findByType(WorkflowType type) {
        return service.findByType(type);
    }

    public List<Workflow> findByStatus(WorkflowStatus status) {
        return service.findByStatus(status);
    }

    public Workflow updateMetadata(WorkflowId id, WorkflowMetadata metadata) {
        return service.updateMetadata(id, metadata);
    }

    public Workflow start(WorkflowId id) {
        return service.start(id);
    }

    public Workflow complete(WorkflowId id) {
        return service.complete(id);
    }

    public Workflow fail(WorkflowId id) {
        return service.fail(id);
    }
}
