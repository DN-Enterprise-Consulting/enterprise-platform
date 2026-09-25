package de.dn.enterprise.platform.assessment.service;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssessmentLifecycleServiceTest {

    private final AssessmentLifecycleService service = new AssessmentLifecycleService();

    private Assessment draft() {
        return Assessment.draft(
                AssessmentType.ENTERPRISE_ARCHITECTURE,
                new AssessmentMetadata("Architecture Assessment", "Initial assessment", java.util.Map.of())
        );
    }

    @Test
    void allowsForwardLifecycleTransitions() {
        Assessment assessment = draft();

        assessment = service.transition(assessment, AssessmentStatus.IN_PROGRESS);
        assertThat(assessment.status()).isEqualTo(AssessmentStatus.IN_PROGRESS);

        assessment = service.transition(assessment, AssessmentStatus.COMPLETED);
        assertThat(assessment.status()).isEqualTo(AssessmentStatus.COMPLETED);

        assessment = service.transition(assessment, AssessmentStatus.ARCHIVED);
        assertThat(assessment.status()).isEqualTo(AssessmentStatus.ARCHIVED);
    }

    @Test
    void rejectsSkippingLifecycleStates() {
        assertThatThrownBy(() -> service.transition(draft(), AssessmentStatus.COMPLETED))
                .isInstanceOf(AssessmentLifecycleException.class);
    }

    @Test
    void rejectsBackwardTransition() {
        Assessment assessment = service.transition(draft(), AssessmentStatus.IN_PROGRESS);

        assertThatThrownBy(() -> service.transition(assessment, AssessmentStatus.DRAFT))
                .isInstanceOf(AssessmentLifecycleException.class);
    }

    @Test
    void archivedIsTerminal() {
        Assessment assessment = draft();
        assessment = service.transition(assessment, AssessmentStatus.IN_PROGRESS);
        assessment = service.transition(assessment, AssessmentStatus.COMPLETED);
        assessment = service.transition(assessment, AssessmentStatus.ARCHIVED);
        Assessment archived = assessment;

        assertThatThrownBy(() -> service.transition(archived, AssessmentStatus.IN_PROGRESS))
                .isInstanceOf(AssessmentLifecycleException.class);
    }

    @Test
    void sameStatusIsIdempotent() {
        Assessment assessment = draft();

        assertThat(service.transition(assessment, AssessmentStatus.DRAFT))
                .isSameAs(assessment);
    }

    @Test
    void rejectsNullArguments() {
        assertThatThrownBy(() -> service.transition(null, AssessmentStatus.DRAFT))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> service.transition(draft(), null))
                .isInstanceOf(NullPointerException.class);
    }
}
