package de.dn.enterprise.platform.assessment.service;

import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;

public final class AssessmentLifecycleException extends RuntimeException {

    public AssessmentLifecycleException(AssessmentStatus currentStatus, AssessmentStatus targetStatus) {
        super("Invalid assessment lifecycle transition: " + currentStatus + " -> " + targetStatus);
    }
}
