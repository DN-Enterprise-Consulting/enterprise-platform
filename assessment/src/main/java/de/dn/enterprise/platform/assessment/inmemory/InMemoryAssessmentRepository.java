package de.dn.enterprise.platform.assessment.inmemory;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentId;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import de.dn.enterprise.platform.assessment.spi.AssessmentRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class InMemoryAssessmentRepository implements AssessmentRepository {

    private final ConcurrentMap<AssessmentId, Assessment> assessments = new ConcurrentHashMap<>();

    @Override
    public Assessment save(Assessment assessment) {
        Objects.requireNonNull(assessment, "assessment must not be null");
        assessments.put(assessment.id(), assessment);
        return assessment;
    }

    @Override
    public Optional<Assessment> findById(AssessmentId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Optional.ofNullable(assessments.get(id));
    }

    @Override
    public List<Assessment> findAll() {
        return assessments.values().stream().toList();
    }

    @Override
    public List<Assessment> findByType(AssessmentType type) {
        Objects.requireNonNull(type, "type must not be null");
        return assessments.values().stream()
                .filter(assessment -> assessment.type() == type)
                .toList();
    }

    @Override
    public List<Assessment> findByStatus(AssessmentStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return assessments.values().stream()
                .filter(assessment -> assessment.status() == status)
                .toList();
    }

    @Override
    public boolean existsById(AssessmentId id) {
        Objects.requireNonNull(id, "id must not be null");
        return assessments.containsKey(id);
    }

    public int size() {
        return assessments.size();
    }
}
