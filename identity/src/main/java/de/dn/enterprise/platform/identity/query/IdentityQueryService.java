package de.dn.enterprise.platform.identity.query;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.spi.IdentityRepository;

import java.util.List;
import java.util.Objects;

public final class IdentityQueryService {

    private final IdentityRepository repository;

    public IdentityQueryService(IdentityRepository repository) {
        this.repository = Objects.requireNonNull(
                repository, "repository must not be null");
    }

    public List<Identity> find(IdentityQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return repository.findAll().stream()
                .filter(identity -> query.type()
                        .map(type -> identity.type() == type)
                        .orElse(true))
                .filter(identity -> query.status()
                        .map(status -> identity.status() == status)
                        .orElse(true))
                .toList();
    }

    public List<Identity> findAll() {
        return find(IdentityQuery.all());
    }
}
