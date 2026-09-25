package de.dn.enterprise.platform.assessment.spi;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentRepositoryContractTest {

    @Test
    void repositoryContractSupportsCoreOperations() {
        AssessmentRepository repository = new StubAssessmentRepository();

        Assessment assessment = Assessment.draft(
                AssessmentType.ENTERPRISE_ARCHITECTURE,
                new AssessmentMetadata("EA", "Assessment", Map.of())
        );

        assertThat(repository.save(assessment)).isEqualTo(assessment);
        assertThat(repository.findById(assessment.id())).contains(assessment);
        assertThat(repository.findAll()).containsExactly(assessment);
        assertThat(repository.findByType(AssessmentType.ENTERPRISE_ARCHITECTURE))
                .containsExactly(assessment);
        assertThat(repository.findByStatus(assessment.status()))
                .containsExactly(assessment);
        assertThat(repository.existsById(assessment.id())).isTrue();
    }

    private static final class StubAssessmentRepository implements AssessmentRepository {

        private Assessment stored;

        @Override
        public Assessment save(Assessment assessment) {
            stored = assessment;
            return assessment;
        }

        @Override
        public Optional<Assessment> findById(de.dn.enterprise.platform.assessment.domain.AssessmentId id) {
            return stored != null && stored.id().equals(id)
                    ? Optional.of(stored)
                    : Optional.empty();
        }

        @Override
        public List<Assessment> findAll() {
            return stored == null ? List.of() : List.of(stored);
        }

        @Override
        public List<Assessment> findByType(AssessmentType type) {
            return stored != null && stored.type() == type ? List.of(stored) : List.of();
        }

        @Override
        public List<Assessment> findByStatus(de.dn.enterprise.platform.assessment.domain.AssessmentStatus status) {
            return stored != null && stored.status() == status ? List.of(stored) : List.of();
        }

        @Override
        public boolean existsById(de.dn.enterprise.platform.assessment.domain.AssessmentId id) {
            return stored != null && stored.id().equals(id);
        }
    }
}
