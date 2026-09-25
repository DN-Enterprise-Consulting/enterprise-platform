package de.dn.enterprise.platform.assessment.spi;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentId;
import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentPersistenceContractTest {

    @Test
    void persistenceContractSupportsSaveLoadAndDelete() {
        AssessmentPersistence persistence = new StubAssessmentPersistence();

        Assessment assessment = Assessment.draft(
                AssessmentType.SOFTWARE_ARCHITECTURE,
                new AssessmentMetadata("Software", "Assessment", Map.of())
        );

        assertThat(persistence.save(assessment)).isEqualTo(assessment);
        assertThat(persistence.load(assessment.id())).contains(assessment);
        assertThat(persistence.loadAll()).containsExactly(assessment);
        assertThat(persistence.exists(assessment.id())).isTrue();

        persistence.delete(assessment.id());

        assertThat(persistence.exists(assessment.id())).isFalse();
        assertThat(persistence.load(assessment.id())).isEmpty();
    }

    private static final class StubAssessmentPersistence implements AssessmentPersistence {

        private final List<Assessment> assessments = new ArrayList<>();

        @Override
        public Assessment save(Assessment assessment) {
            assessments.removeIf(existing -> existing.id().equals(assessment.id()));
            assessments.add(assessment);
            return assessment;
        }

        @Override
        public Optional<Assessment> load(AssessmentId id) {
            return assessments.stream()
                    .filter(assessment -> assessment.id().equals(id))
                    .findFirst();
        }

        @Override
        public List<Assessment> loadAll() {
            return List.copyOf(assessments);
        }

        @Override
        public boolean exists(AssessmentId id) {
            return assessments.stream().anyMatch(assessment -> assessment.id().equals(id));
        }

        @Override
        public void delete(AssessmentId id) {
            assessments.removeIf(assessment -> assessment.id().equals(id));
        }
    }
}
