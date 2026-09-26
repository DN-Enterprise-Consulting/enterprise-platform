package de.dn.enterprise.platform.search.persistent;

import de.dn.enterprise.platform.search.domain.SearchDocument;
import de.dn.enterprise.platform.search.domain.SearchDocumentMetadata;
import de.dn.enterprise.platform.search.domain.SearchDocumentStatus;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;
import de.dn.enterprise.platform.search.persistence.FileSearchDocumentPersistence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersistentSearchDocumentRepositoryTest {

    @TempDir
    Path tempDirectory;

    @Test
    void savesAndLoadsDocumentThroughPersistence() {
        var repository = repository();
        SearchDocument document = document(SearchDocumentType.KNOWLEDGE, "content");

        assertThat(repository.save(document)).isEqualTo(document);
        assertThat(repository.findById(document.id())).isEqualTo(document);
        assertThat(repository.existsById(document.id())).isTrue();
    }

    @Test
    void repositoryCanReadDocumentsAfterBeingRecreated() {
        SearchDocument document = document(SearchDocumentType.KNOWLEDGE, "persistent");
        repository().save(document);

        var recreated = repository();

        assertThat(recreated.findById(document.id())).isEqualTo(document);
        assertThat(recreated.findAll()).containsExactly(document);
    }

    @Test
    void findAllIsSortedById() {
        var repository = repository();
        SearchDocument first = document(SearchDocumentType.KNOWLEDGE, "first");
        SearchDocument second = document(SearchDocumentType.ASSESSMENT, "second");
        repository.save(second);
        repository.save(first);

        assertThat(repository.findAll())
                .extracting(document -> document.id().value())
                .isSorted();
    }

    @Test
    void filtersByTypeAndStatus() {
        var repository = repository();
        SearchDocument knowledge = document(SearchDocumentType.KNOWLEDGE, "knowledge");
        SearchDocument other = document(SearchDocumentType.ASSESSMENT, "other");
        repository.save(knowledge);
        repository.save(other);

        assertThat(repository.findByType(SearchDocumentType.KNOWLEDGE))
                .containsExactly(knowledge);
        assertThat(repository.findByType(SearchDocumentType.ASSESSMENT))
                .containsExactly(other);
        assertThat(repository.findByStatus(SearchDocumentStatus.ACTIVE))
                .containsExactly(knowledge, other);
    }

    @Test
    void missingDocumentReturnsNullAndDoesNotExist() {
        var repository = repository();
        SearchDocument document = document(SearchDocumentType.KNOWLEDGE, "missing");

        assertThat(repository.findById(document.id())).isNull();
        assertThat(repository.existsById(document.id())).isFalse();
    }

    @Test
    void nullArgumentsAreRejected() {
        var repository = repository();
        SearchDocument document = document(SearchDocumentType.KNOWLEDGE, "content");

        assertThatThrownBy(() -> repository.save(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findById(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findByType(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findByStatus(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.existsById(null))
                .isInstanceOf(NullPointerException.class);
        assertThat(document).isNotNull();
    }

    private PersistentSearchDocumentRepository repository() {
        return new PersistentSearchDocumentRepository(
                new FileSearchDocumentPersistence(tempDirectory));
    }

    private static SearchDocument document(SearchDocumentType type, String content) {
        return SearchDocument.create(
                type,
                SearchDocumentMetadata.of("Test", "Description"),
                content,
                Map.of("source", "m9.6"));
    }
}
