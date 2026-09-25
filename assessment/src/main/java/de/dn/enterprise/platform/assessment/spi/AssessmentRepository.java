package de.dn.enterprise.platform.assessment.spi;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentId;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;

import java.util.List;
import java.util.Optional;

public interface AssessmentRepository {

    Assessment save(Assessment assessment);

    Optional<Assessment> findById(AssessmentId id);

    List<Assessment> findAll();

    List<Assessment> findByType(AssessmentType type);

    List<Assessment> findByStatus(AssessmentStatus status);

    boolean existsById(AssessmentId id);
}
