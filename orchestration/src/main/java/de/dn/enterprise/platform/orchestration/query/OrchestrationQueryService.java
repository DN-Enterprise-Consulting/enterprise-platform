package de.dn.enterprise.platform.orchestration.query;

import de.dn.enterprise.platform.orchestration.domain.Orchestration;
import de.dn.enterprise.platform.orchestration.spi.OrchestrationRepository;
import de.dn.enterprise.platform.orchestration.validation.OrchestrationValidator;

import java.util.List;
import java.util.Objects;

public final class OrchestrationQueryService {
    private final OrchestrationRepository repository;
    private final OrchestrationValidator validator;

    public OrchestrationQueryService(OrchestrationRepository repository) {
        this(repository, new OrchestrationValidator());
    }

    public OrchestrationQueryService(OrchestrationRepository repository, OrchestrationValidator validator) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.validator = Objects.requireNonNull(validator, "validator must not be null");
    }

    public List<Orchestration> query(OrchestrationQuery query) {
        Objects.requireNonNull(query, "query must not be null");
        return repository.findAll().stream()
                .peek(validator::validate)
                .filter(orchestration -> query.type().map(type -> orchestration.type() == type).orElse(true))
                .filter(orchestration -> query.status().map(status -> orchestration.status() == status).orElse(true))
                .toList();
    }
}
