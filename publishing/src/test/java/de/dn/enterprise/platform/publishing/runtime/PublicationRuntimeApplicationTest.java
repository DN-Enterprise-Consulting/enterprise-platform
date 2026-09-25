package de.dn.enterprise.platform.publishing.runtime;

import de.dn.enterprise.platform.publishing.domain.PublicationMetadata;
import de.dn.enterprise.platform.publishing.domain.PublicationStatus;
import de.dn.enterprise.platform.publishing.domain.PublicationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class PublicationRuntimeApplicationTest {

    @TempDir
    Path tempDirectory;

    @Test
    void applicationIsPreferredRuntimeEntryPoint() {
        PublicationRuntime runtime =
                PublicationRuntime.fileBacked(tempDirectory);

        var created = runtime.application().create(
                PublicationType.ASSESSMENT_REPORT,
                PublicationMetadata.of("Assessment"));

        runtime.application().publish(created.id());

        assertThat(runtime.application().get(created.id()).status())
                .isEqualTo(PublicationStatus.PUBLISHED);
    }

    @Test
    void applicationPersistsAcrossRuntimeRecreation() {
        PublicationRuntime first =
                PublicationRuntime.fileBacked(tempDirectory);

        var created = first.application().create(
                PublicationType.TECHNICAL_REPORT,
                PublicationMetadata.of("Technical"));
        first.application().publish(created.id());

        PublicationRuntime recreated =
                PublicationRuntime.fileBacked(tempDirectory);

        assertThat(recreated.application().get(created.id()).status())
                .isEqualTo(PublicationStatus.PUBLISHED);
        assertThat(recreated.application().findAll())
                .hasSize(1);
    }
}
