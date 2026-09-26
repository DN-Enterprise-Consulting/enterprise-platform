package de.dn.enterprise.platform.search.inmemory;

import de.dn.enterprise.platform.search.domain.SearchDocument;
import de.dn.enterprise.platform.search.domain.SearchDocumentId;
import de.dn.enterprise.platform.search.domain.SearchDocumentStatus;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;
import de.dn.enterprise.platform.search.spi.SearchDocumentRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** In-memory repository implementation for search documents. */
public final class InMemorySearchDocumentRepository implements SearchDocumentRepository {
    private final ConcurrentMap<SearchDocumentId, SearchDocument> documents = new ConcurrentHashMap<>();

    @Override
    public SearchDocument save(SearchDocument document) {
        Objects.requireNonNull(document, "document must not be null");
        documents.put(document.id(), document);
        return document;
    }

    @Override
    public SearchDocument findById(SearchDocumentId id) {
        Objects.requireNonNull(id, "id must not be null");
        return documents.get(id);
    }

    @Override
    public List<SearchDocument> findAll() {
        return documents.values().stream()
                .sorted(Comparator.comparing(document -> document.id().value()))
                .toList();
    }

    @Override
    public List<SearchDocument> findByType(SearchDocumentType type) {
        Objects.requireNonNull(type, "type must not be null");
        return documents.values().stream()
                .filter(document -> document.type() == type)
                .sorted(Comparator.comparing(document -> document.id().value()))
                .toList();
    }

    @Override
    public List<SearchDocument> findByStatus(SearchDocumentStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return documents.values().stream()
                .filter(document -> document.status() == status)
                .sorted(Comparator.comparing(document -> document.id().value()))
                .toList();
    }

    @Override
    public boolean existsById(SearchDocumentId id) {
        Objects.requireNonNull(id, "id must not be null");
        return documents.containsKey(id);
    }

    @Override
    public boolean deleteById(SearchDocumentId id) {
        Objects.requireNonNull(id, "id must not be null");
        return documents.remove(id) != null;
    }
}
