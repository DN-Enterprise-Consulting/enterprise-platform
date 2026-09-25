package de.dn.enterprise.platform.assessment.persistence;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileAssessmentPersistenceTest {

    @TempDir
    Path tempDirectory;

    private Assessment assessment() {
        return Assessment.draft(
                AssessmentType.ENTERPRISE_ARCHITECTURE,
                new AssessmentMetadata(
                        "Enterprise Architecture",
                        "Assessment description",
                        Map.of("owner", "DN", "priority", "high")
                )
        );
    }

    @Test
    void savesAndLoadsAssessment() {
        FileAssessmentPersistence persistence = new FileAssessmentPersistence(tempDirectory);
        Assessment assessment = assessment();

        persistence.save(assessment);

        assertThat(persistence.load(assessment.id())).contains(assessment);
        assertThat(Files.exists(
                tempDirectory.resolve(assessment.id().value() + ".assessment")))
                .isTrue();
    }

    @Test
    void createsStorageDirectoryWhenSaving() throws Exception {
        Path storage = tempDirectory.resolve("nested").resolve("assessments");
        FileAssessmentPersistence persistence = new FileAssessmentPersistence(storage);

        persistence.save(assessment());

        assertThat(Files.isDirectory(storage)).isTrue();
    }

    @Test
    void loadsAllAssessments() {
        FileAssessmentPersistence persistence = new FileAssessmentPersistence(tempDirectory);
        Assessment first = assessment();
        Assessment second = Assessment.draft(
                AssessmentType.SOFTWARE_ARCHITECTURE,
                new AssessmentMetadata("Software", "Description", Map.of())
        );

        persistence.save(first);
        persistence.save(second);

        assertThat(persistence.loadAll())
                .containsExactlyInAnyOrder(first, second);
    }

    @Test
    void reportsExistenceAndDeletesAssessment() {
        FileAssessmentPersistence persistence = new FileAssessmentPersistence(tempDirectory);
        Assessment assessment = assessment();

        persistence.save(assessment);

        assertThat(persistence.exists(assessment.id())).isTrue();

        persistence.delete(assessment.id());

        assertThat(persistence.exists(assessment.id())).isFalse();
        assertThat(persistence.load(assessment.id())).isEmpty();
    }

    @Test
    void preservesStatusAndMetadata() {
        FileAssessmentPersistence persistence = new FileAssessmentPersistence(tempDirectory);
        Assessment assessment = assessment().withStatus(AssessmentStatus.COMPLETED);

        persistence.save(assessment);

        Assessment loaded = persistence.load(assessment.id()).orElseThrow();

        assertThat(loaded.id()).isEqualTo(assessment.id());
        assertThat(loaded.type()).isEqualTo(assessment.type());
        assertThat(loaded.status()).isEqualTo(AssessmentStatus.COMPLETED);
        assertThat(loaded.metadata()).isEqualTo(assessment.metadata());
    }

    @Test
    void rejectsNullArguments() {
        FileAssessmentPersistence persistence = new FileAssessmentPersistence(tempDirectory);

        assertThatThrownBy(() -> persistence.save(null))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> persistence.load(null))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> persistence.exists(null))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> persistence.delete(null))
                .isInstanceOf(NullPointerException.class);
    }
}
