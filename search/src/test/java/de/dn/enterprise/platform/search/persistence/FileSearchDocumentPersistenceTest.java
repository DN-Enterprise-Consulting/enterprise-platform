package de.dn.enterprise.platform.search.persistence;

import de.dn.enterprise.platform.search.domain.SearchDocument;
import de.dn.enterprise.platform.search.domain.SearchDocumentId;
import de.dn.enterprise.platform.search.domain.SearchDocumentMetadata;
import de.dn.enterprise.platform.search.domain.SearchDocumentStatus;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileSearchDocumentPersistenceTest {

    @TempDir
    Path tempDirectory;

    @Test
    void savesAndLoadsSearchDocumentWithCompleteState() {
        FileSearchDocumentPersistence persistence =
                new FileSearchDocumentPersistence(tempDirectory);
        SearchDocument document = document();

        persistence.save(document);

        assertThat(persistence.exists(document.id())).isTrue();
        assertThat(persistence.load(document.id())).isEqualTo(document);
    }

    @Test
    void preservesUnicodeAndEmbeddedNewlines() {
        FileSearchDocumentPersistence persistence =
                new FileSearchDocumentPersistence(tempDirectory);
        SearchDocument document = SearchDocument.create(
                SearchDocumentType.KNOWLEDGE,
                SearchDocumentMetadata.of("Ärchitektur", "Beschreibung\nmit Zeilenumbruch"),
                "Inhalt\nmit UTF-8: äöü ß",
                Map.of("owner", "DN", "note", "Zeile 1\nZeile 2"));

        persistence.save(document);

        assertThat(persistence.load(document.id())).isEqualTo(document);
    }

    @Test
    void createsRootDirectoryOnFirstSave() {
        Path storage = tempDirectory.resolve("nested").resolve("search");
        FileSearchDocumentPersistence persistence =
                new FileSearchDocumentPersistence(storage);

        SearchDocument document = document();
        persistence.save(document);

        assertThat(Files.exists(storage)).isTrue();
        assertThat(persistence.exists(document.id())).isTrue();
    }

    @Test
    void saveReplacesExistingDocumentWithSameId() {
        FileSearchDocumentPersistence persistence =
                new FileSearchDocumentPersistence(tempDirectory);
        SearchDocument first = document();
        SearchDocument second = new SearchDocument(
                first.id(),
                first.type(),
                SearchDocumentStatus.DELETED,
                SearchDocumentMetadata.of("Updated", "Updated description"),
                "updated content",
                Map.of("version", "2"));

        persistence.save(first);
        persistence.save(second);

        assertThat(persistence.load(first.id())).isEqualTo(second);
    }

    @Test
    void returnsNullForUnknownDocument() {
        FileSearchDocumentPersistence persistence =
                new FileSearchDocumentPersistence(tempDirectory);

        assertThat(persistence.load(SearchDocumentId.random())).isNull();
    }

    @Test
    void deleteRemovesDocument() {
        FileSearchDocumentPersistence persistence =
                new FileSearchDocumentPersistence(tempDirectory);
        SearchDocument document = document();

        persistence.save(document);
        persistence.delete(document.id());

        assertThat(persistence.exists(document.id())).isFalse();
        assertThat(persistence.load(document.id())).isNull();
    }

    @Test
    void deleteOfUnknownDocumentIsNoOp() throws Exception {
        FileSearchDocumentPersistence persistence =
                new FileSearchDocumentPersistence(tempDirectory);

        persistence.delete(SearchDocumentId.random());

        assertThat(Files.list(tempDirectory).count()).isZero();
    }

    @Test
    void rejectsNullArguments() {
        FileSearchDocumentPersistence persistence =
                new FileSearchDocumentPersistence(tempDirectory);

        assertThatThrownBy(() -> persistence.save(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistence.load(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistence.exists(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistence.delete(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsMalformedFiles() throws Exception {
        FileSearchDocumentPersistence persistence =
                new FileSearchDocumentPersistence(tempDirectory);
        SearchDocumentId id = SearchDocumentId.random();
        Path file = tempDirectory.resolve(id.value() + ".searchdocument");

        Files.write(
                file,
                List.of("not-base64"),
                StandardCharsets.UTF_8);

        assertThatThrownBy(() -> persistence.load(id))
                .isInstanceOf(SearchDocumentPersistenceException.class);
    }

    private static SearchDocument document() {
        return SearchDocument.create(
                SearchDocumentType.KNOWLEDGE,
                SearchDocumentMetadata.of(
                        "Architecture",
                        "Enterprise architecture knowledge"),
                "content",
                Map.of("domain", "architecture", "owner", "platform"));
    }
}
