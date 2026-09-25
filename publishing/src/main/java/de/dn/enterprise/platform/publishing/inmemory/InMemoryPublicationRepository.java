package de.dn.enterprise.platform.publishing.inmemory;

import de.dn.enterprise.platform.publishing.domain.Publication;
import de.dn.enterprise.platform.publishing.domain.PublicationId;
import de.dn.enterprise.platform.publishing.domain.PublicationStatus;
import de.dn.enterprise.platform.publishing.domain.PublicationType;
import de.dn.enterprise.platform.publishing.spi.PublicationRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class InMemoryPublicationRepository implements PublicationRepository {

    private final ConcurrentMap<PublicationId, Publication> publications =
            new ConcurrentHashMap<>();

    @Override
    public Publication save(Publication publication) {
        Objects.requireNonNull(publication, "publication must not be null");
        publications.put(publication.id(), publication);
        return publication;
    }

    @Override
    public Optional<Publication> findById(PublicationId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Optional.ofNullable(publications.get(id));
    }

    @Override
    public List<Publication> findAll() {
        return publications.values().stream().toList();
    }

    @Override
    public List<Publication> findByType(PublicationType type) {
        Objects.requireNonNull(type, "type must not be null");
        return publications.values().stream()
                .filter(publication -> publication.type() == type)
                .toList();
    }

    @Override
    public List<Publication> findByStatus(PublicationStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return publications.values().stream()
                .filter(publication -> publication.status() == status)
                .toList();
    }

    @Override
    public boolean existsById(PublicationId id) {
        Objects.requireNonNull(id, "id must not be null");
        return publications.containsKey(id);
    }
}
