package de.dn.enterprise.platform.publishing.service;

import de.dn.enterprise.platform.publishing.domain.Publication;
import de.dn.enterprise.platform.publishing.domain.PublicationId;
import de.dn.enterprise.platform.publishing.domain.PublicationMetadata;
import de.dn.enterprise.platform.publishing.domain.PublicationStatus;
import de.dn.enterprise.platform.publishing.domain.PublicationType;
import de.dn.enterprise.platform.publishing.spi.PublicationRepository;

import java.util.List;
import java.util.Objects;

public final class PersistentPublicationService {

    private final PublicationRepository repository;

    public PersistentPublicationService(PublicationRepository repository) {
        this.repository = Objects.requireNonNull(
                repository, "repository must not be null");
    }

    public Publication create(
            PublicationType type,
            PublicationMetadata metadata) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(metadata, "metadata must not be null");

        return repository.save(Publication.draft(type, metadata));
    }

    public Publication get(PublicationId id) {
        Objects.requireNonNull(id, "id must not be null");
        return repository.findById(id)
                .orElseThrow(() -> new PublicationNotFoundException(id));
    }

    public List<Publication> findAll() {
        return repository.findAll();
    }

    public List<Publication> findByType(PublicationType type) {
        return repository.findByType(
                Objects.requireNonNull(type, "type must not be null"));
    }

    public List<Publication> findByStatus(PublicationStatus status) {
        return repository.findByStatus(
                Objects.requireNonNull(status, "status must not be null"));
    }

    public Publication updateMetadata(
            PublicationId id,
            PublicationMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata must not be null");
        Publication current = get(id);
        ensureMutable(current);

        return repository.save(current.withMetadata(metadata));
    }

    public Publication publish(PublicationId id) {
        return transition(id, PublicationStatus.PUBLISHED);
    }

    public Publication archive(PublicationId id) {
        return transition(id, PublicationStatus.ARCHIVED);
    }

    private Publication transition(
            PublicationId id,
            PublicationStatus target) {
        Publication current = get(id);

        if (!isAllowed(current.status(), target)) {
            throw new PublicationLifecycleException(
                    current.status(), target);
        }

        return repository.save(current.withStatus(target));
    }

    private boolean isAllowed(
            PublicationStatus current,
            PublicationStatus target) {
        return (current == PublicationStatus.DRAFT
                && target == PublicationStatus.PUBLISHED)
                || (current == PublicationStatus.PUBLISHED
                && target == PublicationStatus.ARCHIVED);
    }

    private void ensureMutable(Publication publication) {
        if (publication.status() == PublicationStatus.ARCHIVED) {
            throw new PublicationLifecycleException(
                    publication.status(), publication.status());
        }
    }
}
