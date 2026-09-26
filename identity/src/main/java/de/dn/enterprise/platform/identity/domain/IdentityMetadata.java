package de.dn.enterprise.platform.identity.domain;

import java.util.Objects;

/** Immutable descriptive metadata of an identity. */
public record IdentityMetadata(
        String name,
        String description
) {
    public IdentityMetadata {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(description, "description must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }

    public static IdentityMetadata of(String name, String description) {
        return new IdentityMetadata(name, description);
    }
}
