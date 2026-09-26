package de.dn.enterprise.platform.search.query;

import de.dn.enterprise.platform.search.domain.SearchDocumentStatus;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;

import java.util.Map;
import java.util.Objects;

/** Immutable query for deterministic search retrieval. */
public record SearchQuery(
        String text,
        SearchDocumentType type,
        SearchDocumentStatus status,
        Map<String, String> attributes,
        int limit) {

    public SearchQuery {
        if (text != null && text.isBlank()) {
            text = null;
        }
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        if (limit < 1) {
            throw new IllegalArgumentException("limit must be greater than zero");
        }
    }

    public static SearchQuery all() {
        return new SearchQuery(null, null, null, Map.of(), Integer.MAX_VALUE);
    }

    public static SearchQuery text(String text) {
        return new SearchQuery(Objects.requireNonNull(text, "text must not be null"), null, null, Map.of(), Integer.MAX_VALUE);
    }

    public SearchQuery withType(SearchDocumentType value) {
        return new SearchQuery(text, value, status, attributes, limit);
    }

    public SearchQuery withStatus(SearchDocumentStatus value) {
        return new SearchQuery(text, type, value, attributes, limit);
    }

    public SearchQuery withAttributes(Map<String, String> values) {
        return new SearchQuery(text, type, status, values, limit);
    }

    public SearchQuery withLimit(int value) {
        return new SearchQuery(text, type, status, attributes, value);
    }
}
