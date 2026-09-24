package de.dn.enterprise.platform.sharedkernel.domain;

import java.util.Objects;
import java.util.UUID;

/** Stable technical identifier for domain objects. */
public record Identifier(UUID value) {
    public Identifier {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static Identifier random() {
        return new Identifier(UUID.randomUUID());
    }

    public static Identifier of(String value) {
        Objects.requireNonNull(value, "value must not be null");
        return new Identifier(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
