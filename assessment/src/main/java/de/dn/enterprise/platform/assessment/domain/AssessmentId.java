package de.dn.enterprise.platform.assessment.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Stable identifier for an assessment.
 */
public record AssessmentId(UUID value) {

    public AssessmentId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static AssessmentId newId() {
        return new AssessmentId(UUID.randomUUID());
    }

    public static AssessmentId of(UUID value) {
        return new AssessmentId(value);
    }

    public static AssessmentId parse(String value) {
        Objects.requireNonNull(value, "value must not be null");
        return new AssessmentId(UUID.fromString(value));
    }
}
