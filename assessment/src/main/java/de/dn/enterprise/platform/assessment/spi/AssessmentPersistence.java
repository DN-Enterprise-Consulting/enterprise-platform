package de.dn.enterprise.platform.assessment.spi;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentId;

import java.util.List;
import java.util.Optional;

public interface AssessmentPersistence {

    Assessment save(Assessment assessment);

    Optional<Assessment> load(AssessmentId id);

    List<Assessment> loadAll();

    boolean exists(AssessmentId id);

    void delete(AssessmentId id);
}
