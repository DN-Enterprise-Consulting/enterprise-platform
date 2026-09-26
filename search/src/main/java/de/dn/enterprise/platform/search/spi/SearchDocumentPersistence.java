package de.dn.enterprise.platform.search.spi;

import de.dn.enterprise.platform.search.domain.SearchDocument;
import de.dn.enterprise.platform.search.domain.SearchDocumentId;

import java.util.Objects;

/**
 * Persistence port for search documents.
 *
 * <p>The contract deliberately contains no storage technology concerns.</p>
 */
public interface SearchDocumentPersistence {

    void save(SearchDocument document);

    SearchDocument load(SearchDocumentId id);

    java.util.List<SearchDocument> loadAll();

    boolean exists(SearchDocumentId id);

    void delete(SearchDocumentId id);

    static SearchDocument requireDocument(SearchDocument document) {
        return Objects.requireNonNull(document, "document must not be null");
    }

    static SearchDocumentId requireId(SearchDocumentId id) {
        return Objects.requireNonNull(id, "id must not be null");
    }
}
