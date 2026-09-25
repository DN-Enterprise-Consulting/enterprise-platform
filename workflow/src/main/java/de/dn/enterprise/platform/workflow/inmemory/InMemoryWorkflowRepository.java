package de.dn.enterprise.platform.workflow.inmemory;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.domain.WorkflowId;
import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;
import de.dn.enterprise.platform.workflow.spi.WorkflowRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe in-memory implementation of the workflow repository contract.
 */
public final class InMemoryWorkflowRepository implements WorkflowRepository {

    private final ConcurrentMap<WorkflowId, Workflow> workflows = new ConcurrentHashMap<>();

    @Override
    public Workflow save(Workflow workflow) {
        Objects.requireNonNull(workflow, "workflow must not be null");
        workflows.put(workflow.id(), workflow);
        return workflow;
    }

    @Override
    public Optional<Workflow> findById(WorkflowId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Optional.ofNullable(workflows.get(id));
    }

    @Override
    public List<Workflow> findAll() {
        return List.copyOf(workflows.values());
    }

    @Override
    public List<Workflow> findByType(WorkflowType type) {
        Objects.requireNonNull(type, "type must not be null");
        return workflows.values().stream()
                .filter(workflow -> workflow.type() == type)
                .toList();
    }

    @Override
    public List<Workflow> findByStatus(WorkflowStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return workflows.values().stream()
                .filter(workflow -> workflow.status() == status)
                .toList();
    }

    @Override
    public boolean existsById(WorkflowId id) {
        Objects.requireNonNull(id, "id must not be null");
        return workflows.containsKey(id);
    }
}
