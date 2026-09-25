package de.dn.enterprise.platform.publishing.application;

import de.dn.enterprise.platform.publishing.domain.Publication;
import de.dn.enterprise.platform.publishing.domain.PublicationMetadata;
import de.dn.enterprise.platform.publishing.domain.PublicationStatus;
import de.dn.enterprise.platform.publishing.domain.PublicationType;
import de.dn.enterprise.platform.publishing.inmemory.InMemoryPublicationRepository;
import de.dn.enterprise.platform.publishing.service.PersistentPublicationService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PublicationApplicationTest {

    @Test
    void exposesStableCreateAndGetUseCases() {
        PublicationApplication application =
                new PublicationApplication(
                        new PersistentPublicationService(
                                new InMemoryPublicationRepository()));

        Publication created = application.create(
                PublicationType.ASSESSMENT_REPORT,
                PublicationMetadata.of("Assessment"));

        assertThat(application.get(created.id()))
                .isEqualTo(created);
    }

    @Test
    void exposesQueryUseCases() {
        PublicationApplication application =
                new PublicationApplication(
                        new PersistentPublicationService(
                                new InMemoryPublicationRepository()));

        Publication assessment = application.create(
                PublicationType.ASSESSMENT_REPORT,
                PublicationMetadata.of("Assessment"));
        Publication technical = application.create(
                PublicationType.TECHNICAL_REPORT,
                PublicationMetadata.of("Technical"));

        Publication publishedTechnical =
                application.publish(technical.id());

        assertThat(application.findAll())
                .containsExactlyInAnyOrder(assessment, publishedTechnical);
        assertThat(application.findByType(
                PublicationType.ASSESSMENT_REPORT))
                .containsExactly(assessment);
        assertThat(application.findByStatus(
                PublicationStatus.PUBLISHED))
                .containsExactly(publishedTechnical);
    }

    @Test
    void exposesLifecycleAndUpdateUseCases() {
        PublicationApplication application =
                new PublicationApplication(
                        new PersistentPublicationService(
                                new InMemoryPublicationRepository()));

        Publication created = application.create(
                PublicationType.EXECUTIVE_SUMMARY,
                PublicationMetadata.of("Old"));

        Publication updated = application.updateMetadata(
                created.id(),
                PublicationMetadata.of("New"));
        Publication published = application.publish(updated.id());
        Publication archived = application.archive(published.id());

        assertThat(updated.metadata().name()).isEqualTo("New");
        assertThat(published.status()).isEqualTo(
                PublicationStatus.PUBLISHED);
        assertThat(archived.status()).isEqualTo(
                PublicationStatus.ARCHIVED);
    }

    @Test
    void infrastructureIsNotRequiredByApplicationConstruction() {
        assertThatThrownBy(() -> new PublicationApplication(null))
                .isInstanceOf(NullPointerException.class);
    }
}
