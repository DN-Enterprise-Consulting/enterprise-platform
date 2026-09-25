package de.dn.enterprise.platform.assessment.service;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssessmentServiceTest {

    private final AssessmentService service = new AssessmentService();

    private AssessmentMetadata metadata(String name) {
        return new AssessmentMetadata(name, "Description", Map.of("owner", "DN"));
    }

    @Test
    void createsDraftAssessment() {
        Assessment assessment = service.create(
                AssessmentType.SOFTWARE_ARCHITECTURE,
                metadata("Software Architecture Assessment")
        );

        assertThat(assessment.status()).isEqualTo(AssessmentStatus.DRAFT);
        assertThat(assessment.type()).isEqualTo(AssessmentType.SOFTWARE_ARCHITECTURE);
        assertThat(assessment.metadata().name()).isEqualTo("Software Architecture Assessment");
    }

    @Test
    void updatesMetadataWithoutChangingIdentityOrStatus() {
        Assessment assessment = service.create(
                AssessmentType.APPLICATION_PORTFOLIO,
                metadata("Portfolio")
        );

        Assessment updated = service.updateMetadata(
                assessment,
                metadata("Updated Portfolio")
        );

        assertThat(updated.id()).isEqualTo(assessment.id());
        assertThat(updated.status()).isEqualTo(AssessmentStatus.DRAFT);
        assertThat(updated.metadata().name()).isEqualTo("Updated Portfolio");
    }

    @Test
    void startsCompletesAndArchivesAssessment() {
        Assessment assessment = service.create(
                AssessmentType.ENTERPRISE_ARCHITECTURE,
                metadata("EA Assessment")
        );

        assessment = service.start(assessment);
        assertThat(assessment.status()).isEqualTo(AssessmentStatus.IN_PROGRESS);

        assessment = service.complete(assessment);
        assertThat(assessment.status()).isEqualTo(AssessmentStatus.COMPLETED);

        assessment = service.archive(assessment);
        assertThat(assessment.status()).isEqualTo(AssessmentStatus.ARCHIVED);
    }

    @Test
    void rejectsMetadataUpdateForArchivedAssessment() {
        Assessment assessment = service.create(
                AssessmentType.TECHNOLOGY_LANDSCAPE,
                metadata("Technology Landscape")
        );

        assessment = service.start(assessment);
        assessment = service.complete(assessment);
        assessment = service.archive(assessment);
        Assessment archived = assessment;

        assertThatThrownBy(() -> service.updateMetadata(archived, metadata("Changed")))
                .isInstanceOf(AssessmentLifecycleException.class);
    }

    @Test
    void getRejectsNull() {
        assertThatThrownBy(() -> service.get(null))
                .isInstanceOf(NullPointerException.class);
    }
}
