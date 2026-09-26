package de.dn.enterprise.platform.identity.service;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityId;
import de.dn.enterprise.platform.identity.domain.IdentityMetadata;
import de.dn.enterprise.platform.identity.domain.IdentityStatus;
import de.dn.enterprise.platform.identity.domain.IdentityType;
import de.dn.enterprise.platform.identity.spi.IdentityRepository;
import de.dn.enterprise.platform.identity.validation.IdentityValidator;

import java.util.Objects;

/**
 * Application service for persistent identity management and lifecycle transitions.
 */
public final class PersistentIdentityService {

    private final IdentityRepository repository;
    private final IdentityValidator validator;

    public PersistentIdentityService(IdentityRepository repository) {
        this(repository, new IdentityValidator());
    }

    public PersistentIdentityService(
            IdentityRepository repository,
            IdentityValidator validator) {
        this.repository = Objects.requireNonNull(
                repository, "repository must not be null");
        this.validator = Objects.requireNonNull(
                validator, "validator must not be null");
    }

    public Identity create(IdentityType type, IdentityMetadata metadata) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(metadata, "metadata must not be null");

        Identity identity = Identity.create(type, metadata);
        validator.validate(identity);
        return validatedSave(identity);
    }

    public Identity get(IdentityId id) {
        Objects.requireNonNull(id, "id must not be null");
        Identity identity = repository.findById(id);
        if (identity == null) {
            throw new IdentityNotFoundException(id);
        }
        validator.validate(identity);
        return identity;
    }

    public Identity updateMetadata(IdentityId id, IdentityMetadata metadata) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(metadata, "metadata must not be null");

        Identity current = get(id);
        ensureMutable(current);

        Identity updated = current.withMetadata(metadata);
        validator.validate(updated);
        return validatedSave(updated);
    }

    public Identity lock(IdentityId id) {
        return transition(id, IdentityStatus.LOCKED);
    }

    public Identity disable(IdentityId id) {
        return transition(id, IdentityStatus.DISABLED);
    }

    public Identity activate(IdentityId id) {
        return transition(id, IdentityStatus.ACTIVE);
    }

    private Identity transition(IdentityId id, IdentityStatus targetStatus) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(targetStatus, "targetStatus must not be null");

        Identity current = get(id);
        IdentityStatus currentStatus = current.status();

        if (currentStatus == targetStatus) {
            return current;
        }

        if (!isAllowed(currentStatus, targetStatus)) {
            throw new IdentityLifecycleException(
                    "Invalid identity lifecycle transition: "
                            + currentStatus + " -> " + targetStatus);
        }

        Identity updated = current.withStatus(targetStatus);
        validator.validate(updated);
        return validatedSave(updated);
    }

    private boolean isAllowed(
            IdentityStatus current,
            IdentityStatus target) {
        return switch (current) {
            case ACTIVE -> target == IdentityStatus.LOCKED
                    || target == IdentityStatus.DISABLED;
            case LOCKED -> target == IdentityStatus.ACTIVE
                    || target == IdentityStatus.DISABLED;
            case DISABLED -> false;
        };
    }

    private Identity validatedSave(Identity identity) {
        Identity saved = repository.save(identity);
        validator.validate(saved);
        return saved;
    }

    private void ensureMutable(Identity identity) {
        if (identity.status() == IdentityStatus.DISABLED) {
            throw new IdentityLifecycleException(
                    "Disabled identities cannot be modified: "
                            + identity.id().value());
        }
    }
}
