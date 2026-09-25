package de.dn.enterprise.platform.publishing.persistence;

import de.dn.enterprise.platform.publishing.domain.Publication;
import de.dn.enterprise.platform.publishing.domain.PublicationId;
import de.dn.enterprise.platform.publishing.domain.PublicationMetadata;
import de.dn.enterprise.platform.publishing.domain.PublicationStatus;
import de.dn.enterprise.platform.publishing.domain.PublicationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FilePublicationPersistenceTest {

    @TempDir
    Path tempDirectory;

    @Test
    void savesAndLoadsPublicationRoundTrip() {
        FilePublicationPersistence persistence =
                new FilePublicationPersistence(tempDirectory);

        Publication publication = publication();

        Publication saved = persistence.save(publication);
        Publication loaded = persistence.load(publication.id()).orElseThrow();

        assertThat(saved).isSameAs(publication);
        assertThat(loaded).isEqualTo(publication);
        assertThat(persistence.exists(publication.id())).isTrue();
    }

    @Test
    void createsStorageDirectoryWhenSaving() throws Exception {
        Path storage = tempDirectory.resolve("nested").resolve("publications");
        FilePublicationPersistence persistence =
                new FilePublicationPersistence(storage);

        persistence.save(publication());

        assertThat(Files.isDirectory(storage)).isTrue();
        try (var files = Files.list(storage)) {
            assertThat(files.count()).isEqualTo(1);
        }
    }

    @Test
    void returnsEmptyForMissingPublication() {
        FilePublicationPersistence persistence =
                new FilePublicationPersistence(tempDirectory);

        assertThat(persistence.load(PublicationId.newId())).isEmpty();
    }

    @Test
    void deletesPublication() {
        FilePublicationPersistence persistence =
                new FilePublicationPersistence(tempDirectory);

        Publication publication = publication();
        persistence.save(publication);

        persistence.delete(publication.id());

        assertThat(persistence.exists(publication.id())).isFalse();
        assertThat(persistence.load(publication.id())).isEmpty();
    }

    @Test
    void deleteMissingPublicationIsNoOp() {
        FilePublicationPersistence persistence =
                new FilePublicationPersistence(tempDirectory);

        persistence.delete(PublicationId.newId());
    }

    @Test
    void preservesMetadataAttributesAndStatus() {
        FilePublicationPersistence persistence =
                new FilePublicationPersistence(tempDirectory);

        Publication publication = Publication.draft(
                PublicationType.ARCHITECTURE_DOCUMENT,
                new PublicationMetadata(
                        "Architecture",
                        "Description",
                        Map.of(
                                "owner", "DN",
                                "scope", "enterprise",
                                "unicode", "ÄÖÜ")));

        Publication stored = publication.withStatus(PublicationStatus.PUBLISHED);
        persistence.save(stored);

        assertThat(persistence.load(stored.id())).contains(stored);
    }

    @Test
    void rejectsNullArguments() {
        FilePublicationPersistence persistence =
                new FilePublicationPersistence(tempDirectory);

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
    void storesUtf8AndBase64EncodedValues() throws Exception {
        FilePublicationPersistence persistence =
                new FilePublicationPersistence(tempDirectory);

        Publication publication = Publication.draft(
                PublicationType.TECHNICAL_REPORT,
                new PublicationMetadata(
                        "ÄÖÜ",
                        "Beschreibung mit UTF-8: äöü",
                        Map.of("key", "Wert")));

        persistence.save(publication);

        Path file;
        try (var files = Files.list(tempDirectory)) {
            file = files.findFirst().orElseThrow();
        }

        String raw = Files.readString(file, StandardCharsets.UTF_8);

        assertThat(raw).doesNotContain("ÄÖÜ");
        assertThat(raw).doesNotContain("Beschreibung mit UTF-8");
    }

    @Test
    void malformedFileIsReportedAsPersistenceException() throws Exception {
        FilePublicationPersistence persistence =
                new FilePublicationPersistence(tempDirectory);
        PublicationId id = PublicationId.newId();

        Files.createDirectories(tempDirectory);
        Files.writeString(
                tempDirectory.resolve(id.value() + ".publication"),
                "broken",
                StandardCharsets.UTF_8);

        assertThatThrownBy(() -> persistence.load(id))
                .isInstanceOf(PublicationPersistenceException.class);
    }

    private static Publication publication() {
        return Publication.draft(
                PublicationType.ASSESSMENT_REPORT,
                new PublicationMetadata(
                        "Assessment",
                        "Enterprise assessment",
                        Map.of(
                                "owner", "DN Enterprise Consulting",
                                "scope", "enterprise")));
    }
}
