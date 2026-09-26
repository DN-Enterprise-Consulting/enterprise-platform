package de.dn.enterprise.platform.search.service;

import de.dn.enterprise.platform.search.domain.SearchDocument;
import de.dn.enterprise.platform.search.domain.SearchDocumentId;
import de.dn.enterprise.platform.search.domain.SearchDocumentMetadata;
import de.dn.enterprise.platform.search.domain.SearchDocumentStatus;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;
import de.dn.enterprise.platform.search.spi.SearchDocumentRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Default SearchService implementation backed by a SearchDocumentRepository. */
public final class DefaultSearchService implements SearchService {
    private final SearchDocumentRepository repository;

    public DefaultSearchService(SearchDocumentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    @Override
    public SearchDocument create(SearchDocumentType type, String title, String description,
                                 String content, Map<String, String> attributes) {
        Objects.requireNonNull(type, "type must not be null");
        SearchDocumentMetadata metadata = SearchDocumentMetadata.of(title, description);
        return repository.save(SearchDocument.create(type, metadata, content, attributes));
    }

    @Override
    public SearchDocument get(SearchDocumentId id) {
        Objects.requireNonNull(id, "id must not be null");
        return repository.findById(id);
    }

    @Override
    public SearchDocument update(SearchDocument document) {
        Objects.requireNonNull(document, "document must not be null");
        if (!repository.existsById(document.id())) {
            throw new IllegalArgumentException("search document does not exist: " + document.id());
        }
        return repository.save(document);
    }

    @Override
    public boolean delete(SearchDocumentId id) {
        Objects.requireNonNull(id, "id must not be null");
        return repository.deleteById(id);
    }

    @Override
    public List<SearchDocument> list() {
        return repository.findAll();
    }

    @Override
    public List<SearchDocument> findByType(SearchDocumentType type) {
        Objects.requireNonNull(type, "type must not be null");
        return repository.findByType(type);
    }

    @Override
    public List<SearchDocument> findByStatus(SearchDocumentStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return repository.findByStatus(status);
    }
}
