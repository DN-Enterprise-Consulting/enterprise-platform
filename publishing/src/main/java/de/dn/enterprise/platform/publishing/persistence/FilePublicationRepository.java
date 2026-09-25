package de.dn.enterprise.platform.publishing.persistence;

import de.dn.enterprise.platform.publishing.domain.Publication;
import de.dn.enterprise.platform.publishing.domain.PublicationId;
import de.dn.enterprise.platform.publishing.domain.PublicationStatus;
import de.dn.enterprise.platform.publishing.domain.PublicationType;
import de.dn.enterprise.platform.publishing.spi.PublicationPersistence;
import de.dn.enterprise.platform.publishing.spi.PublicationRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class FilePublicationRepository implements PublicationRepository {

    private final PublicationPersistence persistence;

    public FilePublicationRepository(PublicationPersistence persistence) {
        this.persistence = Objects.requireNonNull(
                persistence, "persistence must not be null");
    }

    @Override
    public Publication save(Publication publication) {
        return persistence.save(
                Objects.requireNonNull(publication, "publication must not be null"));
    }

    @Override
    public Optional<Publication> findById(PublicationId id) {
        return persistence.load(
                Objects.requireNonNull(id, "id must not be null"));
    }

    @Override
    public List<Publication> findAll() {
        if (!(persistence instanceof FilePublicationPersistence filePersistence)) {
            throw new IllegalStateException(
                    "findAll requires FilePublicationPersistence until "
                            + "PublicationPersistence exposes loadAll()");
        }
        return filePersistence.loadAll();
    }

    @Override
    public List<Publication> findByType(PublicationType type) {
        Objects.requireNonNull(type, "type must not be null");
        return findAll().stream()
                .filter(publication -> publication.type() == type)
                .toList();
    }

    @Override
    public List<Publication> findByStatus(PublicationStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return findAll().stream()
                .filter(publication -> publication.status() == status)
                .toList();
    }

    @Override
    public boolean existsById(PublicationId id) {
        return persistence.exists(
                Objects.requireNonNull(id, "id must not be null"));
    }
}
