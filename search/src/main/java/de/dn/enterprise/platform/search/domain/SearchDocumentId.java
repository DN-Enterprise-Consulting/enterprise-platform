package de.dn.enterprise.platform.search.domain;

import java.util.Objects;
import java.util.UUID;

/** Stable technical identifier for a search document. */
public record SearchDocumentId(UUID value) {
    public SearchDocumentId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static SearchDocumentId random() {
        return new SearchDocumentId(UUID.randomUUID());
    }

    public static SearchDocumentId of(String value) {
        Objects.requireNonNull(value, "value must not be null");
        return new SearchDocumentId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
