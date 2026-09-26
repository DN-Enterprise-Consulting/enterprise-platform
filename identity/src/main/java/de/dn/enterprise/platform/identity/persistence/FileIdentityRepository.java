package de.dn.enterprise.platform.identity.persistence;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityId;
import de.dn.enterprise.platform.identity.domain.IdentityStatus;
import de.dn.enterprise.platform.identity.domain.IdentityType;
import de.dn.enterprise.platform.identity.spi.IdentityRepository;

import java.util.List;
import java.util.Objects;

/**
 * Persistent IdentityRepository backed by FileIdentityPersistence.
 */
public final class FileIdentityRepository implements IdentityRepository {

    private final FileIdentityPersistence persistence;

    public FileIdentityRepository(FileIdentityPersistence persistence) {
        this.persistence = Objects.requireNonNull(
                persistence, "persistence must not be null");
    }

    @Override
    public Identity save(Identity identity) {
        Objects.requireNonNull(identity, "identity must not be null");
        persistence.save(identity);
        return identity;
    }

    @Override
    public Identity findById(IdentityId id) {
        Objects.requireNonNull(id, "id must not be null");
        return persistence.load(id);
    }

    @Override
    public List<Identity> findAll() {
        return persistence.loadAll().stream()
                .sorted((left, right) ->
                        left.id().value().compareTo(right.id().value()))
                .toList();
    }

    @Override
    public List<Identity> findByType(IdentityType type) {
        Objects.requireNonNull(type, "type must not be null");
        return findAll().stream()
                .filter(identity -> identity.type() == type)
                .toList();
    }

    @Override
    public List<Identity> findByStatus(IdentityStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return findAll().stream()
                .filter(identity -> identity.status() == status)
                .toList();
    }

    @Override
    public boolean existsById(IdentityId id) {
        Objects.requireNonNull(id, "id must not be null");
        return persistence.exists(id);
    }
}
