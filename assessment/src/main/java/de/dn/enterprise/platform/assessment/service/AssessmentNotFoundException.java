package de.dn.enterprise.platform.assessment.service;

import de.dn.enterprise.platform.assessment.domain.AssessmentId;

import java.util.Objects;

public final class AssessmentNotFoundException extends RuntimeException {

    public AssessmentNotFoundException(AssessmentId id) {
        super("Assessment not found: " + Objects.requireNonNull(id, "id must not be null"));
    }
}
