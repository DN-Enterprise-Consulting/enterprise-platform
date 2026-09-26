package de.dn.enterprise.platform.search.inmemory;

import de.dn.enterprise.platform.search.domain.SearchDocument;
import de.dn.enterprise.platform.search.domain.SearchDocumentId;
import de.dn.enterprise.platform.search.domain.SearchDocumentMetadata;
import de.dn.enterprise.platform.search.domain.SearchDocumentStatus;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemorySearchDocumentRepositoryTest {

    private final InMemorySearchDocumentRepository repository = new InMemorySearchDocumentRepository();

    @Test
    void savesAndFindsById() {
        SearchDocument document = document(SearchDocumentType.KNOWLEDGE, "Knowledge");

        assertThat(repository.save(document)).isSameAs(document);
        assertThat(repository.findById(document.id())).isSameAs(document);
        assertThat(repository.existsById(document.id())).isTrue();
    }

    @Test
    void replacesDocumentWithSameId() {
        SearchDocument first = document(SearchDocumentType.KNOWLEDGE, "First");
        SearchDocument replacement = new SearchDocument(
                first.id(), SearchDocumentType.RULE, SearchDocumentStatus.DELETED,
                SearchDocumentMetadata.of("Replacement", "Updated"), "updated", Map.of("key", "value"));

        repository.save(first);
        assertThat(repository.save(replacement)).isSameAs(replacement);
        assertThat(repository.findById(first.id())).isSameAs(replacement);
        assertThat(repository.findAll()).containsExactly(replacement);
    }

    @Test
    void findsAllDeterministicallyById() {
        SearchDocument first = document(SearchDocumentType.KNOWLEDGE, "First");
        SearchDocument second = document(SearchDocumentType.ASSESSMENT, "Second");
        repository.save(first);
        repository.save(second);

        List<SearchDocument> expected = List.of(first, second).stream()
                .sorted(Comparator.comparing(document -> document.id().value()))
                .toList();

        assertThat(repository.findAll()).containsExactlyElementsOf(expected);
    }

    @Test
    void filtersByType() {
        SearchDocument knowledge = document(SearchDocumentType.KNOWLEDGE, "Knowledge");
        SearchDocument assessment = document(SearchDocumentType.ASSESSMENT, "Assessment");
        SearchDocument anotherKnowledge = document(SearchDocumentType.KNOWLEDGE, "Another Knowledge");
        repository.save(knowledge);
        repository.save(assessment);
        repository.save(anotherKnowledge);

        List<SearchDocument> expected = List.of(knowledge, anotherKnowledge).stream()
                .sorted(Comparator.comparing(document -> document.id().value()))
                .toList();

        assertThat(repository.findByType(SearchDocumentType.KNOWLEDGE))
                .containsExactlyElementsOf(expected);
    }

    @Test
    void filtersByStatus() {
        SearchDocument active = document(SearchDocumentType.KNOWLEDGE, "Active");
        SearchDocument deleted = new SearchDocument(
                SearchDocumentId.random(), SearchDocumentType.KNOWLEDGE, SearchDocumentStatus.DELETED,
                SearchDocumentMetadata.of("Deleted", "Deleted document"), "deleted", Map.of());
        repository.save(active);
        repository.save(deleted);

        assertThat(repository.findByStatus(SearchDocumentStatus.DELETED)).containsExactly(deleted);
        assertThat(repository.findByStatus(SearchDocumentStatus.ACTIVE)).containsExactly(active);
    }

    @Test
    void returnsNullAndFalseForUnknownId() {
        SearchDocumentId id = SearchDocumentId.random();
        assertThat(repository.findById(id)).isNull();
        assertThat(repository.existsById(id)).isFalse();
    }

    @Test
    void rejectsNullArguments() {
        assertThatThrownBy(() -> repository.save(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findById(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findByType(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findByStatus(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.existsById(null)).isInstanceOf(NullPointerException.class);
    }

    private static SearchDocument document(SearchDocumentType type, String name) {
        return SearchDocument.create(type, SearchDocumentMetadata.of(name, name + " description"),
                name + " content", Map.of("source", "test"));
    }
}
