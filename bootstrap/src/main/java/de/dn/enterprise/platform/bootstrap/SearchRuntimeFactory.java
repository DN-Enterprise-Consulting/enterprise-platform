package de.dn.enterprise.platform.bootstrap;

import de.dn.enterprise.platform.search.persistence.FileSearchDocumentPersistence;
import de.dn.enterprise.platform.search.persistent.PersistentSearchDocumentRepository;
import de.dn.enterprise.platform.search.retrieval.DefaultSearchRetrieval;
import de.dn.enterprise.platform.search.retrieval.SearchRetrieval;
import de.dn.enterprise.platform.search.service.DefaultSearchService;
import de.dn.enterprise.platform.search.service.SearchService;
import de.dn.enterprise.platform.search.spi.SearchDocumentPersistence;
import de.dn.enterprise.platform.search.spi.SearchDocumentRepository;

import java.util.Objects;

/** Creates the concrete Search runtime dependency graph. */
public final class SearchRuntimeFactory {
    private SearchRuntimeFactory() {
    }

    public static SearchRuntime create(SearchRuntimeConfiguration configuration) {
        Objects.requireNonNull(configuration, "configuration must not be null");

        SearchDocumentPersistence persistence =
                new FileSearchDocumentPersistence(configuration.storageDirectory());
        SearchDocumentRepository repository =
                new PersistentSearchDocumentRepository(persistence);
        SearchService service = new DefaultSearchService(repository);
        SearchRetrieval retrieval = new DefaultSearchRetrieval(service);

        return new SearchRuntime(persistence, repository, service, retrieval);
    }

    public static SearchRuntime createDefault() {
        return create(SearchRuntimeConfiguration.defaults());
    }
}
