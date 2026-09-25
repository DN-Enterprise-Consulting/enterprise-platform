package de.dn.enterprise.platform.orchestration.persistence;

import de.dn.enterprise.platform.orchestration.domain.Orchestration;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationId;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStatus;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationType;
import de.dn.enterprise.platform.orchestration.spi.OrchestrationPersistence;
import de.dn.enterprise.platform.orchestration.spi.OrchestrationRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * File-backed repository implementation for orchestrations.
 *
 * The repository depends only on the persistence SPI. findAll() uses the
 * concrete file persistence extension until loadAll() becomes part of the
 * generic persistence contract.
 */
public final class FileOrchestrationRepository implements OrchestrationRepository {

    private final OrchestrationPersistence persistence;

    public FileOrchestrationRepository(OrchestrationPersistence persistence) {
        this.persistence = Objects.requireNonNull(persistence, "persistence must not be null");
    }

    @Override
    public Orchestration save(Orchestration orchestration) {
        Objects.requireNonNull(orchestration, "orchestration must not be null");
        persistence.save(orchestration);
        return orchestration;
    }

    @Override
    public Optional<Orchestration> findById(OrchestrationId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Optional.ofNullable(persistence.load(id));
    }

    @Override
    public List<Orchestration> findAll() {
        if (!(persistence instanceof FileOrchestrationPersistence filePersistence)) {
            throw new IllegalStateException(
                    "findAll requires FileOrchestrationPersistence until OrchestrationPersistence exposes loadAll()");
        }
        return filePersistence.loadAll();
    }

    @Override
    public List<Orchestration> findByType(OrchestrationType type) {
        Objects.requireNonNull(type, "type must not be null");
        return findAll().stream()
                .filter(orchestration -> orchestration.type() == type)
                .toList();
    }

    @Override
    public List<Orchestration> findByStatus(OrchestrationStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return findAll().stream()
                .filter(orchestration -> orchestration.status() == status)
                .toList();
    }

    @Override
    public boolean existsById(OrchestrationId id) {
        Objects.requireNonNull(id, "id must not be null");
        return persistence.exists(id);
    }
}
