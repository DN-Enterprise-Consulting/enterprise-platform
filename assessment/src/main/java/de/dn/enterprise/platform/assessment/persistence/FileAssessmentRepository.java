package de.dn.enterprise.platform.assessment.persistence;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentId;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.spi.AssessmentPersistence;
import de.dn.enterprise.platform.assessment.spi.AssessmentRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * File-backed implementation of the assessment repository.
 *
 * <p>The repository delegates persistence to the {@link AssessmentPersistence}
 * port and contains no filesystem-specific logic itself.</p>
 */
public final class FileAssessmentRepository implements AssessmentRepository {

    private final AssessmentPersistence persistence;

    public FileAssessmentRepository(AssessmentPersistence persistence) {
        this.persistence = Objects.requireNonNull(persistence, "persistence must not be null");
    }

    @Override
    public Assessment save(Assessment assessment) {
        return persistence.save(Objects.requireNonNull(assessment, "assessment must not be null"));
    }

    @Override
    public Optional<Assessment> findById(AssessmentId id) {
        return persistence.load(Objects.requireNonNull(id, "id must not be null"));
    }

    @Override
    public List<Assessment> findAll() {
        return persistence.loadAll();
    }

    @Override
    public List<Assessment> findByType(AssessmentType type) {
        Objects.requireNonNull(type, "type must not be null");
        return persistence.loadAll().stream()
                .filter(assessment -> assessment.type() == type)
                .toList();
    }

    @Override
    public List<Assessment> findByStatus(AssessmentStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return persistence.loadAll().stream()
                .filter(assessment -> assessment.status() == status)
                .toList();
    }

    @Override
    public boolean existsById(AssessmentId id) {
        return persistence.exists(Objects.requireNonNull(id, "id must not be null"));
    }
}
