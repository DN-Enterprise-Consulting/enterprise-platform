package de.dn.enterprise.platform.search.spi;

import de.dn.enterprise.platform.search.domain.SearchDocument;
import de.dn.enterprise.platform.search.domain.SearchDocumentId;
import de.dn.enterprise.platform.search.domain.SearchDocumentStatus;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;

import java.util.List;

/** Repository contract for search documents. */
public interface SearchDocumentRepository {
    SearchDocument save(SearchDocument document);
    SearchDocument findById(SearchDocumentId id);
    List<SearchDocument> findAll();
    List<SearchDocument> findByType(SearchDocumentType type);
    List<SearchDocument> findByStatus(SearchDocumentStatus status);
    boolean existsById(SearchDocumentId id);
    boolean deleteById(SearchDocumentId id);
}
