package de.dn.enterprise.platform.identity.domain;

import de.dn.enterprise.platform.sharedkernel.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

/** Stable technical identifier for an identity. */
public record IdentityId(UUID value) {
    public IdentityId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static IdentityId random() {
        return new IdentityId(UUID.randomUUID());
    }

    public static IdentityId of(String value) {
        Objects.requireNonNull(value, "value must not be null");
        return new IdentityId(UUID.fromString(value));
    }

    public Identifier asIdentifier() {
        return new Identifier(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
