package de.dn.enterprise.platform.workflow.spi;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.domain.WorkflowId;
import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;

import java.util.List;
import java.util.Optional;

public interface WorkflowRepository {
    Workflow save(Workflow workflow);
    Optional<Workflow> findById(WorkflowId id);
    List<Workflow> findAll();
    List<Workflow> findByType(WorkflowType type);
    List<Workflow> findByStatus(WorkflowStatus status);
    boolean existsById(WorkflowId id);
}
