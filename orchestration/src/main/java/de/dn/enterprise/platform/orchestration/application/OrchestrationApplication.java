package de.dn.enterprise.platform.orchestration.application;

import de.dn.enterprise.platform.orchestration.domain.Orchestration;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationId;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationMetadata;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStatus;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStep;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationType;
import de.dn.enterprise.platform.orchestration.query.OrchestrationQuery;
import de.dn.enterprise.platform.orchestration.query.OrchestrationQueryService;
import de.dn.enterprise.platform.orchestration.service.PersistentOrchestrationService;

import java.util.List;
import java.util.Objects;

/** Stable application boundary for orchestration use cases. */
public final class OrchestrationApplication {

    private final PersistentOrchestrationService service;
    private final OrchestrationQueryService queryService;

    public OrchestrationApplication(
            PersistentOrchestrationService service,
            OrchestrationQueryService queryService) {
        this.service = Objects.requireNonNull(service, "service must not be null");
        this.queryService = Objects.requireNonNull(queryService, "queryService must not be null");
    }

    public Orchestration create(
            OrchestrationType type,
            OrchestrationMetadata metadata,
            List<OrchestrationStep> steps) {
        return service.create(type, metadata, steps);
    }

    public Orchestration get(OrchestrationId id) {
        return service.get(id);
    }

    public List<Orchestration> findAll() {
        return service.findAll();
    }

    public List<Orchestration> findByType(OrchestrationType type) {
        return service.findByType(type);
    }

    public List<Orchestration> findByStatus(OrchestrationStatus status) {
        return service.findByStatus(status);
    }

    public List<Orchestration> query(OrchestrationQuery query) {
        return queryService.query(query);
    }

    public Orchestration updateMetadata(OrchestrationId id, OrchestrationMetadata metadata) {
        return service.updateMetadata(id, metadata);
    }

    public Orchestration start(OrchestrationId id) {
        return service.start(id);
    }

    public Orchestration complete(OrchestrationId id) {
        return service.complete(id);
    }

    public Orchestration fail(OrchestrationId id) {
        return service.fail(id);
    }
}
