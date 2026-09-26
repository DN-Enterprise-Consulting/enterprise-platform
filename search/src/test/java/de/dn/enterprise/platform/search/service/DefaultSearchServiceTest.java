package de.dn.enterprise.platform.search.service;

import de.dn.enterprise.platform.search.domain.SearchDocument;
import de.dn.enterprise.platform.search.domain.SearchDocumentMetadata;
import de.dn.enterprise.platform.search.domain.SearchDocumentStatus;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;
import de.dn.enterprise.platform.search.inmemory.InMemorySearchDocumentRepository;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultSearchServiceTest {
    @Test
    void createsAndRetrievesDocument() {
        SearchService service = new DefaultSearchService(new InMemorySearchDocumentRepository());
        SearchDocument created = service.create(SearchDocumentType.KNOWLEDGE, "Title", "Description", "content", Map.of("source", "test"));

        assertThat(service.get(created.id())).isEqualTo(created);
        assertThat(service.list()).containsExactly(created);
    }

    @Test
    void updatesExistingDocument() {
        SearchService service = new DefaultSearchService(new InMemorySearchDocumentRepository());
        SearchDocument created = service.create(SearchDocumentType.KNOWLEDGE, "Title", "Description", "old", Map.of());
        SearchDocument updated = new SearchDocument(created.id(), created.type(), SearchDocumentStatus.ACTIVE,
                created.metadata(), "new", created.attributes());

        assertThat(service.update(updated)).isEqualTo(updated);
        assertThat(service.get(created.id())).isEqualTo(updated);
    }

    @Test
    void rejectsUpdateOfUnknownDocument() {
        SearchService service = new DefaultSearchService(new InMemorySearchDocumentRepository());
        SearchDocument document = SearchDocument.create(SearchDocumentType.KNOWLEDGE,
                SearchDocumentMetadata.of("Title", "Description"), "content", Map.of());

        assertThatThrownBy(() -> service.update(document))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deletesDocument() {
        SearchService service = new DefaultSearchService(new InMemorySearchDocumentRepository());
        SearchDocument created = service.create(SearchDocumentType.KNOWLEDGE, "Title", "Description", "content", Map.of());

        assertThat(service.delete(created.id())).isTrue();
        assertThat(service.get(created.id())).isNull();
        assertThat(service.delete(created.id())).isFalse();
    }

    @Test
    void filtersDocuments() {
        SearchService service = new DefaultSearchService(new InMemorySearchDocumentRepository());
        SearchDocument knowledge = service.create(SearchDocumentType.KNOWLEDGE, "K", "D", "content", Map.of());
        service.create(SearchDocumentType.RULE, "R", "D", "content", Map.of());

        assertThat(service.findByType(SearchDocumentType.KNOWLEDGE)).containsExactly(knowledge);
        assertThat(service.findByStatus(SearchDocumentStatus.ACTIVE)).hasSize(2);
    }

    @Test
    void rejectsNullRepository() {
        assertThatThrownBy(() -> new DefaultSearchService(null))
                .isInstanceOf(NullPointerException.class);
    }
}
