package de.dn.enterprise.platform.search.retrieval;

import de.dn.enterprise.platform.search.domain.SearchDocument;
import de.dn.enterprise.platform.search.domain.SearchDocumentMetadata;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;
import de.dn.enterprise.platform.search.inmemory.InMemorySearchDocumentRepository;
import de.dn.enterprise.platform.search.query.SearchQuery;
import de.dn.enterprise.platform.search.service.DefaultSearchService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultSearchRetrievalHardeningTest {

    @Test
    void nullQueryIsRejected() {
        var retrieval = new DefaultSearchRetrieval(new DefaultSearchService(new InMemorySearchDocumentRepository()));

        assertThatThrownBy(() -> retrieval.search(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("query must not be null");
    }

    @Test
    void noTextQueryReturnsFilteredDocuments() {
        var repository = new InMemorySearchDocumentRepository();
        var service = new DefaultSearchService(repository);
        service.create(SearchDocumentType.KNOWLEDGE, "A", "Desc", "one", Map.of("source", "x"));
        service.create(SearchDocumentType.KNOWLEDGE, "B", "Desc", "two", Map.of("source", "y"));

        var retrieval = new DefaultSearchRetrieval(service);

        assertThat(retrieval.search(SearchQuery.all().withAttributes(Map.of("source", "x"))))
                .hasSize(1)
                .allSatisfy(result -> assertThat(result.document().attributes()).containsEntry("source", "x"));
    }

    @Test
    void limitIsAppliedAfterRanking() {
        var repository = new InMemorySearchDocumentRepository();
        var service = new DefaultSearchService(repository);
        service.create(SearchDocumentType.KNOWLEDGE, "alpha alpha", "", "alpha", Map.of());
        service.create(SearchDocumentType.KNOWLEDGE, "alpha", "", "", Map.of());

        var retrieval = new DefaultSearchRetrieval(service);

        List<?> results = retrieval.search(SearchQuery.text("alpha").withLimit(1));

        assertThat(results).hasSize(1);
        assertThat(((de.dn.enterprise.platform.search.query.SearchResult) results.get(0)).score()).isGreaterThan(0);
    }

    @Test
    void emptyRepositoryReturnsEmptyResults() {
        var retrieval = new DefaultSearchRetrieval(new DefaultSearchService(new InMemorySearchDocumentRepository()));

        assertThat(retrieval.search(SearchQuery.text("missing"))).isEmpty();
    }
}
