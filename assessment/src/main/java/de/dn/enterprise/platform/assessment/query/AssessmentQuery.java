package de.dn.enterprise.platform.assessment.query;

import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import java.util.Optional;

public record AssessmentQuery(Optional<AssessmentType> type, Optional<AssessmentStatus> status) {
    public AssessmentQuery {
        type = type == null ? Optional.empty() : type;
        status = status == null ? Optional.empty() : status;
    }
    public static AssessmentQuery all() { return new AssessmentQuery(Optional.empty(), Optional.empty()); }
    public static AssessmentQuery byType(AssessmentType type) { return new AssessmentQuery(Optional.of(type), Optional.empty()); }
    public static AssessmentQuery byStatus(AssessmentStatus status) { return new AssessmentQuery(Optional.empty(), Optional.of(status)); }
    public static AssessmentQuery byTypeAndStatus(AssessmentType type, AssessmentStatus status) { return new AssessmentQuery(Optional.of(type), Optional.of(status)); }
}
