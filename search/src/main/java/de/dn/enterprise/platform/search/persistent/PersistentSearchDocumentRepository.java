package de.dn.enterprise.platform.search.persistent;

import de.dn.enterprise.platform.search.domain.SearchDocument;
import de.dn.enterprise.platform.search.domain.SearchDocumentId;
import de.dn.enterprise.platform.search.domain.SearchDocumentStatus;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;
import de.dn.enterprise.platform.search.spi.SearchDocumentPersistence;
import de.dn.enterprise.platform.search.spi.SearchDocumentRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Persistent repository implementation backed by the SearchDocumentPersistence SPI.
 *
 * <p>The repository deliberately keeps persistence concerns behind the SPI and
 * implements repository-level filtering by loading persisted documents.</p>
 */
public final class PersistentSearchDocumentRepository implements SearchDocumentRepository {

    private final SearchDocumentPersistence persistence;

    public PersistentSearchDocumentRepository(SearchDocumentPersistence persistence) {
        this.persistence = Objects.requireNonNull(
                persistence, "persistence must not be null");
    }

    @Override
    public SearchDocument save(SearchDocument document) {
        Objects.requireNonNull(document, "document must not be null");
        persistence.save(document);
        return document;
    }

    @Override
    public SearchDocument findById(SearchDocumentId id) {
        Objects.requireNonNull(id, "id must not be null");
        return persistence.load(id);
    }

    @Override
    public List<SearchDocument> findAll() {
        return documentsFromPersistence().stream()
                .sorted(Comparator.comparing(document -> document.id().value()))
                .toList();
    }

    @Override
    public List<SearchDocument> findByType(SearchDocumentType type) {
        Objects.requireNonNull(type, "type must not be null");
        return documentsFromPersistence().stream()
                .filter(document -> document.type() == type)
                .sorted(Comparator.comparing(document -> document.id().value()))
                .toList();
    }

    @Override
    public List<SearchDocument> findByStatus(SearchDocumentStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return documentsFromPersistence().stream()
                .filter(document -> document.status() == status)
                .sorted(Comparator.comparing(document -> document.id().value()))
                .toList();
    }

    @Override
    public boolean existsById(SearchDocumentId id) {
        Objects.requireNonNull(id, "id must not be null");
        return persistence.exists(id);
    }

    @Override
    public boolean deleteById(SearchDocumentId id) {
        Objects.requireNonNull(id, "id must not be null");
        if (!persistence.exists(id)) {
            return false;
        }
        persistence.delete(id);
        return true;
    }

    private List<SearchDocument> documentsFromPersistence() {
        return persistence.loadAll();
    }
}
