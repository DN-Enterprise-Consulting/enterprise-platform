package de.dn.enterprise.platform.workflow.service;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.domain.WorkflowId;
import de.dn.enterprise.platform.workflow.domain.WorkflowMetadata;
import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;
import de.dn.enterprise.platform.workflow.spi.WorkflowRepository;

import java.util.List;
import java.util.Objects;

public final class PersistentWorkflowService {

    private final WorkflowRepository repository;

    public PersistentWorkflowService(WorkflowRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    public Workflow create(WorkflowType type, WorkflowMetadata metadata) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(metadata, "metadata must not be null");
        return repository.save(Workflow.draft(type, metadata));
    }

    public Workflow get(WorkflowId id) {
        Objects.requireNonNull(id, "id must not be null");
        return repository.findById(id)
                .orElseThrow(() -> new WorkflowNotFoundException(id));
    }

    public List<Workflow> findAll() {
        return repository.findAll();
    }

    public List<Workflow> findByType(WorkflowType type) {
        return repository.findByType(Objects.requireNonNull(type, "type must not be null"));
    }

    public List<Workflow> findByStatus(WorkflowStatus status) {
        return repository.findByStatus(Objects.requireNonNull(status, "status must not be null"));
    }

    public Workflow updateMetadata(WorkflowId id, WorkflowMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata must not be null");
        Workflow current = get(id);
        ensureMutable(current);
        return repository.save(current.withMetadata(metadata));
    }

    public Workflow start(WorkflowId id) {
        return transition(id, WorkflowStatus.RUNNING);
    }

    public Workflow complete(WorkflowId id) {
        return transition(id, WorkflowStatus.COMPLETED);
    }

    public Workflow fail(WorkflowId id) {
        return transition(id, WorkflowStatus.FAILED);
    }

    private Workflow transition(WorkflowId id, WorkflowStatus target) {
        Workflow current = get(id);
        if (!isAllowed(current.status(), target)) {
            throw new WorkflowLifecycleException(current.status(), target);
        }
        return repository.save(current.withStatus(target));
    }

    private boolean isAllowed(WorkflowStatus current, WorkflowStatus target) {
        return (current == WorkflowStatus.DRAFT && target == WorkflowStatus.RUNNING)
                || (current == WorkflowStatus.RUNNING
                && (target == WorkflowStatus.COMPLETED || target == WorkflowStatus.FAILED));
    }

    private void ensureMutable(Workflow workflow) {
        if (workflow.status() != WorkflowStatus.DRAFT) {
            throw new WorkflowLifecycleException(workflow.status(), workflow.status());
        }
    }
}
