package de.dn.enterprise.platform.assessment.service;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentId;
import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;

import java.util.Map;
import java.util.Objects;

public final class AssessmentService {

    private final AssessmentLifecycleService lifecycleService;

    public AssessmentService() {
        this(new AssessmentLifecycleService());
    }

    public AssessmentService(AssessmentLifecycleService lifecycleService) {
        this.lifecycleService = Objects.requireNonNull(lifecycleService, "lifecycleService must not be null");
    }

    public Assessment create(AssessmentType type, AssessmentMetadata metadata) {
        return Assessment.draft(type, metadata);
    }

    public Assessment get(Assessment assessment) {
        return Objects.requireNonNull(assessment, "assessment must not be null");
    }

    public Assessment updateMetadata(Assessment assessment, AssessmentMetadata metadata) {
        Objects.requireNonNull(assessment, "assessment must not be null");
        Objects.requireNonNull(metadata, "metadata must not be null");

        if (assessment.status() == AssessmentStatus.ARCHIVED) {
            throw new AssessmentLifecycleException(assessment.status(), AssessmentStatus.ARCHIVED);
        }

        return assessment.withMetadata(metadata);
    }

    public Assessment start(Assessment assessment) {
        return lifecycleService.transition(assessment, AssessmentStatus.IN_PROGRESS);
    }

    public Assessment complete(Assessment assessment) {
        return lifecycleService.transition(assessment, AssessmentStatus.COMPLETED);
    }

    public Assessment archive(Assessment assessment) {
        return lifecycleService.transition(assessment, AssessmentStatus.ARCHIVED);
    }
}
