package de.dn.enterprise.platform.search.retrieval;

import de.dn.enterprise.platform.search.domain.SearchDocument;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;
import de.dn.enterprise.platform.search.inmemory.InMemorySearchDocumentRepository;
import de.dn.enterprise.platform.search.query.SearchQuery;
import de.dn.enterprise.platform.search.query.SearchResult;
import de.dn.enterprise.platform.search.service.DefaultSearchService;
import de.dn.enterprise.platform.search.service.SearchService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultSearchRetrievalTest {
    @Test
    void retrievesCaseInsensitiveTextAndRanksNameHigher() {
        SearchService service = new DefaultSearchService(new InMemorySearchDocumentRepository());
        SearchDocument first = service.create(SearchDocumentType.KNOWLEDGE, "Java Architecture", "desc", "unrelated", Map.of());
        SearchDocument second = service.create(SearchDocumentType.KNOWLEDGE, "Other", "desc", "Java is used", Map.of());

        List<SearchResult> results = new DefaultSearchRetrieval(service).search(SearchQuery.text("java"));

        assertThat(results).extracting(SearchResult::document)
                .containsExactly(first, second);
        assertThat(results.get(0).score()).isGreaterThan(results.get(1).score());
    }

    @Test
    void appliesTypeAndAttributeFilters() {
        SearchService service = new DefaultSearchService(new InMemorySearchDocumentRepository());
        SearchDocument match = service.create(SearchDocumentType.KNOWLEDGE, "A", "", "Java", Map.of("source", "kb"));
        service.create(SearchDocumentType.RULE, "B", "", "Java", Map.of("source", "kb"));
        service.create(SearchDocumentType.KNOWLEDGE, "C", "", "Java", Map.of("source", "other"));

        SearchQuery query = SearchQuery.text("java")
                .withType(SearchDocumentType.KNOWLEDGE)
                .withAttributes(Map.of("source", "kb"));

        assertThat(new DefaultSearchRetrieval(service).search(query))
                .extracting(SearchResult::document)
                .containsExactly(match);
    }

    @Test
    void respectsLimitAndReturnsEmptyWhenNothingMatches() {
        SearchService service = new DefaultSearchService(new InMemorySearchDocumentRepository());
        service.create(SearchDocumentType.KNOWLEDGE, "A", "", "Java", Map.of());
        service.create(SearchDocumentType.KNOWLEDGE, "B", "", "Java", Map.of());

        assertThat(new DefaultSearchRetrieval(service).search(SearchQuery.text("Java").withLimit(1))).hasSize(1);
        assertThat(new DefaultSearchRetrieval(service).search(SearchQuery.text("Kubernetes"))).isEmpty();
    }
}
