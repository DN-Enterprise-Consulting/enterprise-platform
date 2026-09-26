package de.dn.enterprise.platform.identity.application;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityId;
import de.dn.enterprise.platform.identity.domain.IdentityMetadata;
import de.dn.enterprise.platform.identity.domain.IdentityStatus;
import de.dn.enterprise.platform.identity.domain.IdentityType;
import de.dn.enterprise.platform.identity.query.IdentityQuery;
import de.dn.enterprise.platform.identity.query.IdentityQueryService;
import de.dn.enterprise.platform.identity.service.PersistentIdentityService;

import java.util.List;
import java.util.Objects;

/** Stable application boundary for identity use cases. */
public final class IdentityApplication {

    private final PersistentIdentityService service;
    private final IdentityQueryService queryService;

    public IdentityApplication(
            PersistentIdentityService service,
            IdentityQueryService queryService) {
        this.service = Objects.requireNonNull(service, "service must not be null");
        this.queryService = Objects.requireNonNull(queryService, "queryService must not be null");
    }

    public Identity create(IdentityType type, IdentityMetadata metadata) {
        return service.create(type, metadata);
    }

    public Identity get(IdentityId id) {
        return service.get(id);
    }

    public List<Identity> findAll() {
        return queryService.findAll();
    }

    public List<Identity> find(IdentityQuery query) {
        return queryService.find(query);
    }

    public List<Identity> findByType(IdentityType type) {
        return queryService.find(IdentityQuery.byType(type));
    }

    public List<Identity> findByStatus(IdentityStatus status) {
        return queryService.find(IdentityQuery.byStatus(status));
    }

    public List<Identity> findByTypeAndStatus(
            IdentityType type,
            IdentityStatus status) {
        return queryService.find(IdentityQuery.byTypeAndStatus(type, status));
    }

    public Identity updateMetadata(IdentityId id, IdentityMetadata metadata) {
        return service.updateMetadata(id, metadata);
    }

    public Identity activate(IdentityId id) {
        return service.activate(id);
    }

    public Identity lock(IdentityId id) {
        return service.lock(id);
    }

    public Identity disable(IdentityId id) {
        return service.disable(id);
    }
}
