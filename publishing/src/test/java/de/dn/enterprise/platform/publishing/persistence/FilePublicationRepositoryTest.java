package de.dn.enterprise.platform.publishing.persistence;

import de.dn.enterprise.platform.publishing.domain.Publication;
import de.dn.enterprise.platform.publishing.domain.PublicationMetadata;
import de.dn.enterprise.platform.publishing.domain.PublicationStatus;
import de.dn.enterprise.platform.publishing.domain.PublicationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FilePublicationRepositoryTest {

    @TempDir
    Path tempDirectory;

    @Test
    void persistsAndFindsPublicationById() {
        FilePublicationPersistence persistence =
                new FilePublicationPersistence(tempDirectory);
        FilePublicationRepository repository =
                new FilePublicationRepository(persistence);

        Publication publication = publication(
                PublicationType.ASSESSMENT_REPORT,
                PublicationStatus.DRAFT);

        repository.save(publication);

        assertThat(repository.findById(publication.id()))
                .contains(publication);
        assertThat(repository.existsById(publication.id()))
                .isTrue();
    }

    @Test
    void findAllReadsAllPersistedPublications() {
        FilePublicationPersistence persistence =
                new FilePublicationPersistence(tempDirectory);
        FilePublicationRepository repository =
                new FilePublicationRepository(persistence);

        Publication first = publication(
                PublicationType.ASSESSMENT_REPORT,
                PublicationStatus.DRAFT);
        Publication second = publication(
                PublicationType.TECHNICAL_REPORT,
                PublicationStatus.PUBLISHED);

        repository.save(first);
        repository.save(second);

        assertThat(repository.findAll())
                .containsExactlyInAnyOrder(first, second);
    }

    @Test
    void filtersByTypeAndStatusUsingPersistentData() {
        FilePublicationPersistence persistence =
                new FilePublicationPersistence(tempDirectory);
        FilePublicationRepository repository =
                new FilePublicationRepository(persistence);

        Publication assessment = publication(
                PublicationType.ASSESSMENT_REPORT,
                PublicationStatus.DRAFT);
        Publication technical = publication(
                PublicationType.TECHNICAL_REPORT,
                PublicationStatus.PUBLISHED);
        Publication architecture = publication(
                PublicationType.ARCHITECTURE_DOCUMENT,
                PublicationStatus.DRAFT);

        repository.save(assessment);
        repository.save(technical);
        repository.save(architecture);

        assertThat(repository.findByType(PublicationType.TECHNICAL_REPORT))
                .containsExactly(technical);

        assertThat(repository.findByStatus(PublicationStatus.DRAFT))
                .containsExactlyInAnyOrder(assessment, architecture);
    }

    @Test
    void survivesRepositoryRecreation() {
        FilePublicationPersistence persistence =
                new FilePublicationPersistence(tempDirectory);
        FilePublicationRepository firstRepository =
                new FilePublicationRepository(persistence);

        Publication publication = publication(
                PublicationType.EXECUTIVE_SUMMARY,
                PublicationStatus.PUBLISHED);

        firstRepository.save(publication);

        FilePublicationRepository recreatedRepository =
                new FilePublicationRepository(
                        new FilePublicationPersistence(tempDirectory));

        assertThat(recreatedRepository.findById(publication.id()))
                .contains(publication);
        assertThat(recreatedRepository.findAll())
                .containsExactly(publication);
    }

    @Test
    void missingPublicationIsNotFound() {
        FilePublicationRepository repository =
                new FilePublicationRepository(
                        new FilePublicationPersistence(tempDirectory));

        assertThat(repository.findById(
                de.dn.enterprise.platform.publishing.domain.PublicationId.newId()))
                .isEmpty();
    }

    @Test
    void rejectsNullArguments() {
        FilePublicationRepository repository =
                new FilePublicationRepository(
                        new FilePublicationPersistence(tempDirectory));

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
    }

    @Test
    void rejectsNonFilePersistenceForFindAllUntilSpiSupportsLoadAll() {
        PublicationPersistenceStub stub = new PublicationPersistenceStub();
        FilePublicationRepository repository =
                new FilePublicationRepository(stub);

        assertThatThrownBy(repository::findAll)
                .isInstanceOf(IllegalStateException.class);
    }

    private static Publication publication(
            PublicationType type,
            PublicationStatus status) {
        return Publication.draft(
                type,
                PublicationMetadata.of(type.name()))
                .withStatus(status);
    }

    private static final class PublicationPersistenceStub
            implements de.dn.enterprise.platform.publishing.spi.PublicationPersistence {

        @Override
        public Publication save(Publication publication) {
            return publication;
        }

        @Override
        public java.util.Optional<Publication> load(
                de.dn.enterprise.platform.publishing.domain.PublicationId id) {
            return java.util.Optional.empty();
        }

        @Override
        public boolean exists(
                de.dn.enterprise.platform.publishing.domain.PublicationId id) {
            return false;
        }

        @Override
        public void delete(
                de.dn.enterprise.platform.publishing.domain.PublicationId id) {
        }
    }
}
