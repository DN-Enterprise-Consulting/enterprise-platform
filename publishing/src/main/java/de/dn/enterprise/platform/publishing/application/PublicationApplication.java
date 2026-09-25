package de.dn.enterprise.platform.publishing.application;

import de.dn.enterprise.platform.publishing.domain.Publication;
import de.dn.enterprise.platform.publishing.domain.PublicationId;
import de.dn.enterprise.platform.publishing.domain.PublicationMetadata;
import de.dn.enterprise.platform.publishing.domain.PublicationStatus;
import de.dn.enterprise.platform.publishing.domain.PublicationType;
import de.dn.enterprise.platform.publishing.service.PersistentPublicationService;

import java.util.List;
import java.util.Objects;

public final class PublicationApplication {

    private final PersistentPublicationService service;

    public PublicationApplication(PersistentPublicationService service) {
        this.service = Objects.requireNonNull(
                service, "service must not be null");
    }

    public Publication create(
            PublicationType type,
            PublicationMetadata metadata) {
        return service.create(type, metadata);
    }

    public Publication get(PublicationId id) {
        return service.get(id);
    }

    public List<Publication> findAll() {
        return service.findAll();
    }

    public List<Publication> findByType(PublicationType type) {
        return service.findByType(type);
    }

    public List<Publication> findByStatus(PublicationStatus status) {
        return service.findByStatus(status);
    }

    public Publication updateMetadata(
            PublicationId id,
            PublicationMetadata metadata) {
        return service.updateMetadata(id, metadata);
    }

    public Publication publish(PublicationId id) {
        return service.publish(id);
    }

    public Publication archive(PublicationId id) {
        return service.archive(id);
    }
}
