package de.dn.enterprise.platform.assessment.validation;

import de.dn.enterprise.platform.assessment.domain.Assessment;

import java.util.Objects;

public final class AssessmentValidator {

    public void validate(Assessment assessment) {
        if (assessment == null) {
            throw new AssessmentValidationException("assessment must not be null");
        }

        if (assessment.type() == null) {
            throw new AssessmentValidationException("assessment type must not be null");
        }

        if (assessment.status() == null) {
            throw new AssessmentValidationException("assessment status must not be null");
        }

        if (assessment.metadata() == null) {
            throw new AssessmentValidationException("assessment metadata must not be null");
        }

        if (assessment.metadata().name() == null
                || assessment.metadata().name().isBlank()) {
            throw new AssessmentValidationException("assessment metadata name must not be blank");
        }
    }
}
