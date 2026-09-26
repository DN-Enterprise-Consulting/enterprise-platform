package de.dn.enterprise.platform.identity.inmemory;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityId;
import de.dn.enterprise.platform.identity.domain.IdentityStatus;
import de.dn.enterprise.platform.identity.domain.IdentityType;
import de.dn.enterprise.platform.identity.spi.IdentityRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe in-memory implementation of the IdentityRepository contract.
 */
public final class InMemoryIdentityRepository implements IdentityRepository {

    private final ConcurrentMap<IdentityId, Identity> identities = new ConcurrentHashMap<>();

    @Override
    public Identity save(Identity identity) {
        Objects.requireNonNull(identity, "identity must not be null");
        identities.put(identity.id(), identity);
        return identity;
    }

    @Override
    public Identity findById(IdentityId id) {
        Objects.requireNonNull(id, "id must not be null");
        return identities.get(id);
    }

    @Override
    public List<Identity> findAll() {
        return sorted(identities.values());
    }

    @Override
    public List<Identity> findByType(IdentityType type) {
        Objects.requireNonNull(type, "type must not be null");
        return sorted(identities.values().stream()
                .filter(identity -> identity.type() == type)
                .toList());
    }

    @Override
    public List<Identity> findByStatus(IdentityStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return sorted(identities.values().stream()
                .filter(identity -> identity.status() == status)
                .toList());
    }

    @Override
    public boolean existsById(IdentityId id) {
        Objects.requireNonNull(id, "id must not be null");
        return identities.containsKey(id);
    }

    private List<Identity> sorted(Iterable<Identity> source) {
        List<Identity> result = new ArrayList<>();
        source.forEach(result::add);
        result.sort(Comparator.comparing(identity -> identity.id().value()));
        return List.copyOf(result);
    }
}
