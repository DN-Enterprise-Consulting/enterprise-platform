package de.dn.enterprise.platform.assessment.service;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;

import java.util.Objects;

public final class AssessmentLifecycleService {

    public Assessment transition(Assessment assessment, AssessmentStatus targetStatus) {
        Objects.requireNonNull(assessment, "assessment must not be null");
        Objects.requireNonNull(targetStatus, "targetStatus must not be null");

        if (assessment.status() == targetStatus) {
            return assessment;
        }

        if (!isAllowed(assessment.status(), targetStatus)) {
            throw new AssessmentLifecycleException(assessment.status(), targetStatus);
        }

        return assessment.withStatus(targetStatus);
    }

    private boolean isAllowed(AssessmentStatus current, AssessmentStatus target) {
        return switch (current) {
            case DRAFT -> target == AssessmentStatus.IN_PROGRESS;
            case IN_PROGRESS -> target == AssessmentStatus.COMPLETED;
            case COMPLETED -> target == AssessmentStatus.ARCHIVED;
            case ARCHIVED -> false;
        };
    }
}
