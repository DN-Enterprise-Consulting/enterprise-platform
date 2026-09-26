package de.dn.enterprise.platform.search.query;

import de.dn.enterprise.platform.search.domain.SearchDocument;

import java.util.Objects;

/** A retrieved search document with its deterministic relevance score. */
public record SearchResult(SearchDocument document, int score) {
    public SearchResult {
        Objects.requireNonNull(document, "document must not be null");
        if (score < 0) {
            throw new IllegalArgumentException("score must not be negative");
        }
    }
}
