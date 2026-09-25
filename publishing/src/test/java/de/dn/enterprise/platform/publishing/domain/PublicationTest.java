package de.dn.enterprise.platform.publishing.domain;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PublicationTest {

    @Test
    void createsDraftPublication() {
        PublicationMetadata metadata =
                new PublicationMetadata("Assessment Report", "Description", Map.of("format", "pdf"));

        Publication publication = Publication.draft(PublicationType.ASSESSMENT_REPORT, metadata);

        assertThat(publication.id()).isNotNull();
        assertThat(publication.type()).isEqualTo(PublicationType.ASSESSMENT_REPORT);
        assertThat(publication.status()).isEqualTo(PublicationStatus.DRAFT);
        assertThat(publication.metadata()).isEqualTo(metadata);
    }

    @Test
    void changesStatusWithoutChangingIdentity() {
        Publication publication =
                Publication.draft(PublicationType.TECHNICAL_REPORT, PublicationMetadata.of("Technical Report"));

        Publication published = publication.withStatus(PublicationStatus.PUBLISHED);

        assertThat(published.id()).isEqualTo(publication.id());
        assertThat(published.status()).isEqualTo(PublicationStatus.PUBLISHED);
        assertThat(published.type()).isEqualTo(publication.type());
    }

    @Test
    void changesMetadataWithoutChangingIdentityOrStatus() {
        Publication publication =
                Publication.draft(PublicationType.ARCHITECTURE_DOCUMENT, PublicationMetadata.of("Architecture"));

        Publication updated = publication.withMetadata(
                new PublicationMetadata("Architecture v2", "Updated", Map.of("version", "2")));

        assertThat(updated.id()).isEqualTo(publication.id());
        assertThat(updated.status()).isEqualTo(PublicationStatus.DRAFT);
        assertThat(updated.metadata().name()).isEqualTo("Architecture v2");
    }

    @Test
    void validatesRequiredFields() {
        PublicationId id = PublicationId.of(UUID.randomUUID());
        PublicationMetadata metadata = PublicationMetadata.of("Report");

        assertThatThrownBy(() -> new Publication(null, PublicationType.ASSESSMENT_REPORT,
                PublicationStatus.DRAFT, metadata))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new Publication(id, null, PublicationStatus.DRAFT, metadata))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new Publication(id, PublicationType.ASSESSMENT_REPORT,
                null, metadata))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new Publication(id, PublicationType.ASSESSMENT_REPORT,
                PublicationStatus.DRAFT, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void metadataCopiesAttributes() {
        PublicationMetadata metadata =
                new PublicationMetadata("Report", null, Map.of("format", "pdf"));

        assertThat(metadata.description()).isEmpty();
        assertThat(metadata.attributes()).containsEntry("format", "pdf");
    }
}
