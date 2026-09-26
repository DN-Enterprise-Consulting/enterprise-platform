package de.dn.enterprise.platform.search.domain;

import java.util.Objects;

public record SearchDocumentMetadata(String name, String description) {
    public SearchDocumentMetadata {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(description, "description must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
    }

    public static SearchDocumentMetadata of(String name, String description) {
        return new SearchDocumentMetadata(name, description);
    }
}
