package de.dn.enterprise.platform.publishing.domain;

import java.util.Objects;
import java.util.UUID;

public record PublicationId(UUID value) {
    public PublicationId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static PublicationId newId() {
        return new PublicationId(UUID.randomUUID());
    }

    public static PublicationId of(UUID value) {
        return new PublicationId(value);
    }

    public static PublicationId parse(String value) {
        return new PublicationId(UUID.fromString(value));
    }
}
