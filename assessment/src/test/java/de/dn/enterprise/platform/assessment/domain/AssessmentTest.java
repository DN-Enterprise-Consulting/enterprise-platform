package de.dn.enterprise.platform.assessment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.Test;

class AssessmentTest {

    @Test
    void createsDraftAssessment() {
        var metadata = new AssessmentMetadata("Architecture Assessment", "Initial assessment", Map.of("owner", "DN"));

        var assessment = Assessment.draft(AssessmentType.ENTERPRISE_ARCHITECTURE, metadata);

        assertThat(assessment.id()).isNotNull();
        assertThat(assessment.type()).isEqualTo(AssessmentType.ENTERPRISE_ARCHITECTURE);
        assertThat(assessment.status()).isEqualTo(AssessmentStatus.DRAFT);
        assertThat(assessment.metadata()).isEqualTo(metadata);
    }

    @Test
    void changesStatusWithoutChangingIdentity() {
        var assessment = Assessment.draft(
                AssessmentType.SOFTWARE_ARCHITECTURE,
                new AssessmentMetadata("Software Assessment", "", Map.of()));

        var updated = assessment.withStatus(AssessmentStatus.IN_PROGRESS);

        assertThat(updated.id()).isEqualTo(assessment.id());
        assertThat(updated.type()).isEqualTo(assessment.type());
        assertThat(updated.status()).isEqualTo(AssessmentStatus.IN_PROGRESS);
    }

    @Test
    void rejectsBlankMetadataName() {
        assertThatThrownBy(() -> new AssessmentMetadata(" ", "", Map.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name must not be blank");
    }

    @Test
    void copiesMetadataAttributes() {
        var metadata = new AssessmentMetadata("Assessment", null, Map.of("key", "value"));

        assertThat(metadata.description()).isEmpty();
        assertThat(metadata.attributes()).containsEntry("key", "value");
    }
}
