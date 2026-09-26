package de.dn.enterprise.platform.bootstrap;

import de.dn.enterprise.platform.search.retrieval.SearchRetrieval;
import de.dn.enterprise.platform.search.service.SearchService;
import de.dn.enterprise.platform.search.spi.SearchDocumentPersistence;
import de.dn.enterprise.platform.search.spi.SearchDocumentRepository;

import java.util.Objects;

/** Fully composed Search runtime. */
public final class SearchRuntime {
    private final SearchDocumentPersistence persistence;
    private final SearchDocumentRepository repository;
    private final SearchService service;
    private final SearchRetrieval retrieval;

    SearchRuntime(
            SearchDocumentPersistence persistence,
            SearchDocumentRepository repository,
            SearchService service,
            SearchRetrieval retrieval) {
        this.persistence = Objects.requireNonNull(persistence, "persistence must not be null");
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.service = Objects.requireNonNull(service, "service must not be null");
        this.retrieval = Objects.requireNonNull(retrieval, "retrieval must not be null");
    }

    public SearchDocumentPersistence persistence() {
        return persistence;
    }

    public SearchDocumentRepository repository() {
        return repository;
    }

    public SearchService service() {
        return service;
    }

    public SearchRetrieval retrieval() {
        return retrieval;
    }
}
