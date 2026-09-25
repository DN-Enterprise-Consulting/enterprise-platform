package de.dn.enterprise.platform.publishing.inmemory;

import de.dn.enterprise.platform.publishing.domain.Publication;
import de.dn.enterprise.platform.publishing.domain.PublicationId;
import de.dn.enterprise.platform.publishing.domain.PublicationMetadata;
import de.dn.enterprise.platform.publishing.domain.PublicationStatus;
import de.dn.enterprise.platform.publishing.domain.PublicationType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryPublicationRepositoryTest {

    @Test
    void savesAndFindsPublicationById() {
        InMemoryPublicationRepository repository = new InMemoryPublicationRepository();
        Publication publication = publication(PublicationType.ASSESSMENT_REPORT);

        Publication saved = repository.save(publication);

        assertThat(saved).isSameAs(publication);
        assertThat(repository.findById(publication.id())).containsSame(publication);
    }

    @Test
    void returnsEmptyForUnknownId() {
        InMemoryPublicationRepository repository = new InMemoryPublicationRepository();

        assertThat(repository.findById(PublicationId.newId())).isEmpty();
    }

    @Test
    void findsAllPublications() {
        InMemoryPublicationRepository repository = new InMemoryPublicationRepository();
        Publication first = publication(PublicationType.ASSESSMENT_REPORT);
        Publication second = publication(PublicationType.TECHNICAL_REPORT);

        repository.save(first);
        repository.save(second);

        assertThat(repository.findAll())
                .containsExactlyInAnyOrder(first, second);
    }

    @Test
    void filtersByType() {
        InMemoryPublicationRepository repository = new InMemoryPublicationRepository();
        Publication assessment = publication(PublicationType.ASSESSMENT_REPORT);
        Publication technical = publication(PublicationType.TECHNICAL_REPORT);

        repository.save(assessment);
        repository.save(technical);

        assertThat(repository.findByType(PublicationType.ASSESSMENT_REPORT))
                .containsExactly(assessment);
    }

    @Test
    void filtersByStatus() {
        InMemoryPublicationRepository repository = new InMemoryPublicationRepository();
        Publication draft = publication(PublicationType.ASSESSMENT_REPORT);
        Publication published = publication(PublicationType.TECHNICAL_REPORT)
                .withStatus(PublicationStatus.PUBLISHED);

        repository.save(draft);
        repository.save(published);

        assertThat(repository.findByStatus(PublicationStatus.DRAFT))
                .containsExactly(draft);
        assertThat(repository.findByStatus(PublicationStatus.PUBLISHED))
                .containsExactly(published);
    }

    @Test
    void replacesPublicationWithSameId() {
        InMemoryPublicationRepository repository = new InMemoryPublicationRepository();
        Publication original = publication(PublicationType.ASSESSMENT_REPORT);
        Publication replacement = original.withStatus(PublicationStatus.PUBLISHED);

        repository.save(original);
        Publication saved = repository.save(replacement);

        assertThat(saved).isSameAs(replacement);
        assertThat(repository.findById(original.id())).containsSame(replacement);
        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void supportsExistenceCheck() {
        InMemoryPublicationRepository repository = new InMemoryPublicationRepository();
        Publication publication = publication(PublicationType.EXECUTIVE_SUMMARY);

        assertThat(repository.existsById(publication.id())).isFalse();

        repository.save(publication);

        assertThat(repository.existsById(publication.id())).isTrue();
    }

    @Test
    void rejectsNullArguments() {
        InMemoryPublicationRepository repository = new InMemoryPublicationRepository();
        Publication publication = publication(PublicationType.ASSESSMENT_REPORT);

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

        assertThat(repository.save(publication)).isSameAs(publication);
    }

    private static Publication publication(PublicationType type) {
        return Publication.draft(
                type,
                PublicationMetadata.of(type.name()));
    }
}
