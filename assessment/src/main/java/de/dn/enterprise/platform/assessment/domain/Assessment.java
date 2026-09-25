package de.dn.enterprise.platform.assessment.domain;

import java.util.Objects;

/**
 * Core assessment aggregate.
 */
public record Assessment(
        AssessmentId id,
        AssessmentType type,
        AssessmentStatus status,
        AssessmentMetadata metadata) {

    public Assessment {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(metadata, "metadata must not be null");
    }

    public static Assessment draft(AssessmentType type, AssessmentMetadata metadata) {
        return new Assessment(AssessmentId.newId(), type, AssessmentStatus.DRAFT, metadata);
    }

    public Assessment withStatus(AssessmentStatus newStatus) {
        return new Assessment(id, type, newStatus, metadata);
    }

    public Assessment withMetadata(AssessmentMetadata newMetadata) {
        return new Assessment(id, type, status, newMetadata);
    }
}
