package de.dn.enterprise.platform.identity.domain;

import java.util.Objects;

/**
 * Core identity aggregate.
 *
 * Security credentials, authentication protocols and authorization policies are
 * intentionally outside this domain foundation and will be introduced through
 * explicit application/SPI boundaries in later milestones.
 */
public record Identity(
        IdentityId id,
        IdentityType type,
        IdentityStatus status,
        IdentityMetadata metadata
) {
    public Identity {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(metadata, "metadata must not be null");
    }

    public static Identity create(IdentityType type, IdentityMetadata metadata) {
        return new Identity(IdentityId.random(), type, IdentityStatus.ACTIVE, metadata);
    }

    public Identity withStatus(IdentityStatus newStatus) {
        return new Identity(id, type, Objects.requireNonNull(newStatus, "newStatus must not be null"), metadata);
    }

    public Identity withMetadata(IdentityMetadata newMetadata) {
        return new Identity(id, type, status, Objects.requireNonNull(newMetadata, "newMetadata must not be null"));
    }
}
