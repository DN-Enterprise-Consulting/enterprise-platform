package de.dn.enterprise.platform.workflow.repository;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.domain.WorkflowId;
import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;
import de.dn.enterprise.platform.workflow.spi.WorkflowPersistence;
import de.dn.enterprise.platform.workflow.spi.WorkflowRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * File-backed implementation of the workflow repository.
 *
 * <p>The repository delegates persistence to the workflow persistence SPI.
 * Query operations are reconstructed from the persisted workflow objects.
 */
public final class FileWorkflowRepository implements WorkflowRepository {

    private final WorkflowPersistence persistence;

    public FileWorkflowRepository(WorkflowPersistence persistence) {
        this.persistence = Objects.requireNonNull(
                persistence, "persistence must not be null");
    }

    @Override
    public Workflow save(Workflow workflow) {
        Objects.requireNonNull(workflow, "workflow must not be null");
        return persistence.save(workflow);
    }

    @Override
    public Optional<Workflow> findById(WorkflowId id) {
        Objects.requireNonNull(id, "id must not be null");
        return persistence.load(id);
    }

    @Override
    public List<Workflow> findAll() {
        return loadAll().stream().toList();
    }

    @Override
    public List<Workflow> findByType(WorkflowType type) {
        Objects.requireNonNull(type, "type must not be null");
        return findAll().stream()
                .filter(workflow -> workflow.type() == type)
                .toList();
    }

    @Override
    public List<Workflow> findByStatus(WorkflowStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return findAll().stream()
                .filter(workflow -> workflow.status() == status)
                .toList();
    }

    @Override
    public boolean existsById(WorkflowId id) {
        Objects.requireNonNull(id, "id must not be null");
        return persistence.exists(id);
    }

    private List<Workflow> loadAll() {
        if (persistence instanceof de.dn.enterprise.platform.workflow.persistence.FileWorkflowPersistence filePersistence) {
            return filePersistence.loadAll();
        }
        throw new IllegalStateException(
                "FileWorkflowRepository requires FileWorkflowPersistence");
    }
}
