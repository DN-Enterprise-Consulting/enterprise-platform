package de.dn.enterprise.platform.publishing.service;

import de.dn.enterprise.platform.publishing.domain.Publication;
import de.dn.enterprise.platform.publishing.domain.PublicationMetadata;
import de.dn.enterprise.platform.publishing.domain.PublicationStatus;
import de.dn.enterprise.platform.publishing.domain.PublicationType;
import de.dn.enterprise.platform.publishing.inmemory.InMemoryPublicationRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersistentPublicationServiceTest {

    @Test
    void createsAndReadsPublication() {
        PersistentPublicationService service =
                new PersistentPublicationService(
                        new InMemoryPublicationRepository());

        Publication publication = service.create(
                PublicationType.ASSESSMENT_REPORT,
                PublicationMetadata.of("Assessment"));

        assertThat(publication.status()).isEqualTo(PublicationStatus.DRAFT);
        assertThat(service.get(publication.id())).isEqualTo(publication);
    }

    @Test
    void updatesMetadata() {
        PersistentPublicationService service =
                new PersistentPublicationService(
                        new InMemoryPublicationRepository());

        Publication publication = service.create(
                PublicationType.TECHNICAL_REPORT,
                PublicationMetadata.of("Old"));

        Publication updated = service.updateMetadata(
                publication.id(),
                PublicationMetadata.of("New"));

        assertThat(updated.id()).isEqualTo(publication.id());
        assertThat(updated.metadata().name()).isEqualTo("New");
        assertThat(updated.status()).isEqualTo(PublicationStatus.DRAFT);
    }

    @Test
    void publishesAndArchives() {
        PersistentPublicationService service =
                new PersistentPublicationService(
                        new InMemoryPublicationRepository());

        Publication publication = service.create(
                PublicationType.EXECUTIVE_SUMMARY,
                PublicationMetadata.of("Summary"));

        Publication published = service.publish(publication.id());
        Publication archived = service.archive(publication.id());

        assertThat(published.status()).isEqualTo(PublicationStatus.PUBLISHED);
        assertThat(archived.status()).isEqualTo(PublicationStatus.ARCHIVED);
    }

    @Test
    void rejectsInvalidLifecycleTransitions() {
        PersistentPublicationService service =
                new PersistentPublicationService(
                        new InMemoryPublicationRepository());

        Publication publication = service.create(
                PublicationType.ARCHITECTURE_DOCUMENT,
                PublicationMetadata.of("Architecture"));

        assertThatThrownBy(() -> service.archive(publication.id()))
                .isInstanceOf(PublicationLifecycleException.class);

        service.publish(publication.id());

        assertThatThrownBy(() -> service.publish(publication.id()))
                .isInstanceOf(PublicationLifecycleException.class);
    }

    @Test
    void archivedPublicationCannotBeModified() {
        PersistentPublicationService service =
                new PersistentPublicationService(
                        new InMemoryPublicationRepository());

        Publication publication = service.create(
                PublicationType.TECHNICAL_REPORT,
                PublicationMetadata.of("Technical"));

        service.publish(publication.id());
        service.archive(publication.id());

        assertThatThrownBy(() -> service.updateMetadata(
                publication.id(),
                PublicationMetadata.of("Changed")))
                .isInstanceOf(PublicationLifecycleException.class);
    }

    @Test
    void rejectsUnknownPublication() {
        PersistentPublicationService service =
                new PersistentPublicationService(
                        new InMemoryPublicationRepository());

        var id = de.dn.enterprise.platform.publishing.domain.PublicationId.newId();

        assertThatThrownBy(() -> service.get(id))
                .isInstanceOf(PublicationNotFoundException.class);
    }
}
