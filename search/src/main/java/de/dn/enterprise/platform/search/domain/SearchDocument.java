package de.dn.enterprise.platform.search.domain;

import java.util.Map;
import java.util.Objects;

public record SearchDocument(
        SearchDocumentId id,
        SearchDocumentType type,
        SearchDocumentStatus status,
        SearchDocumentMetadata metadata,
        String content,
        Map<String, String> attributes) {

    public SearchDocument {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(metadata, "metadata must not be null");
        Objects.requireNonNull(content, "content must not be null");
        Objects.requireNonNull(attributes, "attributes must not be null");
        attributes = Map.copyOf(attributes);
    }

    public static SearchDocument create(
            SearchDocumentType type,
            SearchDocumentMetadata metadata,
            String content,
            Map<String, String> attributes) {
        return new SearchDocument(
                SearchDocumentId.random(),
                type,
                SearchDocumentStatus.ACTIVE,
                metadata,
                content,
                attributes);
    }
}
