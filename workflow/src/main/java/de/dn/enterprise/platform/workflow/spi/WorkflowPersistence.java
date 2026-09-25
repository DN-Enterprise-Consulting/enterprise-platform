package de.dn.enterprise.platform.workflow.spi;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.domain.WorkflowId;

import java.util.Optional;

public interface WorkflowPersistence {

    Workflow save(Workflow workflow);

    Optional<Workflow> load(WorkflowId id);

    boolean exists(WorkflowId id);

    void delete(WorkflowId id);
}
