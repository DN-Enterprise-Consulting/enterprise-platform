package de.dn.enterprise.platform.assessment.persistence;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileAssessmentRepositoryTest {

    @TempDir
    Path tempDirectory;

    @Test
    void savesAndFindsAssessmentById() {
        var repository = repository();
        var assessment = assessment("Architecture");

        repository.save(assessment);

        assertThat(repository.findById(assessment.id())).contains(assessment);
        assertThat(repository.existsById(assessment.id())).isTrue();
    }

    @Test
    void findsAllAssessments() {
        var repository = repository();
        var first = assessment("First");
        var second = assessment("Second");

        repository.save(first);
        repository.save(second);

        assertThat(repository.findAll()).containsExactlyInAnyOrder(first, second);
    }

    @Test
    void findsAssessmentsByType() {
        var repository = repository();
        var architecture = assessment("Architecture");
        var portfolio = Assessment.draft(
                AssessmentType.APPLICATION_PORTFOLIO,
                new AssessmentMetadata("Portfolio", "Description", Map.of())
        );

        repository.save(architecture);
        repository.save(portfolio);

        assertThat(repository.findByType(AssessmentType.ENTERPRISE_ARCHITECTURE))
                .containsExactly(architecture);
    }

    @Test
    void findsAssessmentsByStatus() {
        var repository = repository();
        var draft = assessment("Draft");
        var completed = Assessment.draft(
                AssessmentType.ENTERPRISE_ARCHITECTURE,
                new AssessmentMetadata("Completed", "Description", Map.of())
        ).withStatus(AssessmentStatus.COMPLETED);

        repository.save(draft);
        repository.save(completed);

        assertThat(repository.findByStatus(AssessmentStatus.COMPLETED))
                .containsExactly(completed);
    }

    @Test
    void returnsEmptyForUnknownId() {
        assertThat(repository().findById(de.dn.enterprise.platform.assessment.domain.AssessmentId.newId()))
                .isEmpty();
    }

    @Test
    void rejectsNullArguments() {
        var repository = repository();

        assertThatThrownBy(() -> repository.save(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findById(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findByType(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.existsById(null))
                .isInstanceOf(NullPointerException.class);
    }

    private FileAssessmentRepository repository() {
        return new FileAssessmentRepository(
                new FileAssessmentPersistence(tempDirectory)
        );
    }

    private Assessment assessment(String name) {
        return Assessment.draft(
                AssessmentType.ENTERPRISE_ARCHITECTURE,
                new AssessmentMetadata(name, "Description", Map.of("owner", "DN"))
        );
    }
}
