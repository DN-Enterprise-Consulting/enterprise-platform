package de.dn.enterprise.platform.publishing.runtime;

import de.dn.enterprise.platform.publishing.domain.PublicationMetadata;
import de.dn.enterprise.platform.publishing.domain.PublicationStatus;
import de.dn.enterprise.platform.publishing.domain.PublicationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class PublicationRuntimeTest {

    @TempDir
    Path tempDirectory;

    @Test
    void persistsPublicationAcrossRuntimeRecreation() {
        PublicationRuntime firstRuntime =
                PublicationRuntime.fileBacked(tempDirectory);

        var created = firstRuntime.publicationService().create(
                PublicationType.ASSESSMENT_REPORT,
                PublicationMetadata.of("Persistent Report"));

        firstRuntime.publicationService().publish(created.id());

        PublicationRuntime recreated =
                PublicationRuntime.fileBacked(tempDirectory);

        assertThat(recreated.publicationService().get(created.id()).status())
                .isEqualTo(PublicationStatus.PUBLISHED);
        assertThat(recreated.publicationService().findAll())
                .containsExactly(created.withStatus(PublicationStatus.PUBLISHED));
    }

    @Test
    void runtimeUsesFileBackedStorage() {
        PublicationRuntime runtime =
                PublicationRuntime.fileBacked(tempDirectory);

        var created = runtime.publicationService().create(
                PublicationType.TECHNICAL_REPORT,
                PublicationMetadata.of("Technical"));

        assertThat(tempDirectory.resolve(
                created.id().value() + ".publication")).exists();
    }
}
