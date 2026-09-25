package de.dn.enterprise.platform.assessment.inmemory;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryAssessmentRepositoryTest {

    private final InMemoryAssessmentRepository repository = new InMemoryAssessmentRepository();

    private Assessment assessment(AssessmentType type, String name) {
        return Assessment.draft(
                type,
                new AssessmentMetadata(name, "Assessment", Map.of())
        );
    }

    @Test
    void savesAndFindsAssessmentById() {
        Assessment assessment = assessment(
                AssessmentType.ENTERPRISE_ARCHITECTURE,
                "EA"
        );

        repository.save(assessment);

        assertThat(repository.findById(assessment.id())).contains(assessment);
        assertThat(repository.existsById(assessment.id())).isTrue();
    }

    @Test
    void returnsEmptyForUnknownId() {
        Assessment assessment = assessment(
                AssessmentType.SOFTWARE_ARCHITECTURE,
                "Software"
        );

        assertThat(repository.findById(assessment.id())).isEmpty();
        assertThat(repository.existsById(assessment.id())).isFalse();
    }

    @Test
    void findsAllAssessments() {
        Assessment first = assessment(
                AssessmentType.ENTERPRISE_ARCHITECTURE,
                "EA"
        );
        Assessment second = assessment(
                AssessmentType.APPLICATION_PORTFOLIO,
                "Portfolio"
        );

        repository.save(first);
        repository.save(second);

        assertThat(repository.findAll()).containsExactlyInAnyOrder(first, second);
        assertThat(repository.size()).isEqualTo(2);
    }

    @Test
    void filtersByType() {
        Assessment ea = assessment(
                AssessmentType.ENTERPRISE_ARCHITECTURE,
                "EA"
        );
        Assessment software = assessment(
                AssessmentType.SOFTWARE_ARCHITECTURE,
                "Software"
        );

        repository.save(ea);
        repository.save(software);

        assertThat(repository.findByType(AssessmentType.ENTERPRISE_ARCHITECTURE))
                .containsExactly(ea);
    }

    @Test
    void filtersByStatus() {
        Assessment draft = assessment(
                AssessmentType.ENTERPRISE_ARCHITECTURE,
                "Draft"
        );
        Assessment completed = assessment(
                AssessmentType.SOFTWARE_ARCHITECTURE,
                "Completed"
        ).withStatus(AssessmentStatus.COMPLETED);

        repository.save(draft);
        repository.save(completed);

        assertThat(repository.findByStatus(AssessmentStatus.DRAFT))
                .containsExactly(draft);
        assertThat(repository.findByStatus(AssessmentStatus.COMPLETED))
                .containsExactly(completed);
    }

    @Test
    void replacesAssessmentWithSameId() {
        Assessment original = assessment(
                AssessmentType.ENTERPRISE_ARCHITECTURE,
                "Original"
        );
        Assessment replacement = original.withStatus(AssessmentStatus.IN_PROGRESS);

        repository.save(original);
        repository.save(replacement);

        assertThat(repository.findById(original.id())).contains(replacement);
        assertThat(repository.size()).isEqualTo(1);
    }

    @Test
    void rejectsNullArguments() {
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
    }
}
