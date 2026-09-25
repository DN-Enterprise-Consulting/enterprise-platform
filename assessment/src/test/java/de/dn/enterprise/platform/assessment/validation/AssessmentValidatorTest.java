package de.dn.enterprise.platform.assessment.validation;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssessmentValidatorTest {

    private final AssessmentValidator validator = new AssessmentValidator();

    @Test
    void acceptsValidAssessment() {
        Assessment assessment = Assessment.draft(
                AssessmentType.ENTERPRISE_ARCHITECTURE,
                new AssessmentMetadata(
                        "Architecture Assessment",
                        "description",
                        Map.of()
                )
        );

        assertThatCode(() -> validator.validate(assessment))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsNullAssessment() {
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(AssessmentValidationException.class)
                .hasMessage("assessment must not be null");
    }

    @Test
    void domainGuaranteesNonNullTypeStatusAndMetadata() {
        Assessment assessment = Assessment.draft(
                AssessmentType.ENTERPRISE_ARCHITECTURE,
                new AssessmentMetadata("Assessment", "", Map.of())
        );

        assertThatCode(() -> validator.validate(assessment))
                .doesNotThrowAnyException();
    }
}
