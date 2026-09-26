package de.dn.enterprise.platform.search.service;

import de.dn.enterprise.platform.search.domain.SearchDocument;
import de.dn.enterprise.platform.search.domain.SearchDocumentId;
import de.dn.enterprise.platform.search.domain.SearchDocumentStatus;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;

import java.util.List;
import java.util.Map;

/** Application-facing service for managing search documents. */
public interface SearchService {
    SearchDocument create(SearchDocumentType type,
                          String title,
                          String description,
                          String content,
                          Map<String, String> attributes);

    SearchDocument get(SearchDocumentId id);

    SearchDocument update(SearchDocument document);

    boolean delete(SearchDocumentId id);

    List<SearchDocument> list();

    List<SearchDocument> findByType(SearchDocumentType type);

    List<SearchDocument> findByStatus(SearchDocumentStatus status);
}
