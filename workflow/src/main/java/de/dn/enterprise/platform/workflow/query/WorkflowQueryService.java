package de.dn.enterprise.platform.workflow.query;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.spi.WorkflowRepository;

import java.util.List;
import java.util.Objects;

public final class WorkflowQueryService {

    private final WorkflowRepository repository;

    public WorkflowQueryService(WorkflowRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    public List<Workflow> query(WorkflowQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return repository.findAll().stream()
                .filter(workflow -> query.type().map(type -> workflow.type() == type).orElse(true))
                .filter(workflow -> query.status().map(status -> workflow.status() == status).orElse(true))
                .toList();
    }
}
