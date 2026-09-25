package de.dn.enterprise.platform.assessment.api;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentId;
import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import de.dn.enterprise.platform.assessment.query.AssessmentQueryService;
import de.dn.enterprise.platform.assessment.service.PersistentAssessmentService;

import java.util.List;
import java.util.Objects;

/**
 * Stable application-facing Assessment API.
 *
 * This facade deliberately hides repositories, persistence implementations,
 * validators and lifecycle services from callers at the application boundary.
 */
public final class AssessmentApplication {

    private final PersistentAssessmentService service;
    private final AssessmentQueryService queryService;

    public AssessmentApplication(
            PersistentAssessmentService service,
            AssessmentQueryService queryService) {
        this.service = Objects.requireNonNull(service, "service must not be null");
        this.queryService = Objects.requireNonNull(queryService, "queryService must not be null");
    }

    public Assessment create(AssessmentType type, AssessmentMetadata metadata) {
        return service.create(type, metadata);
    }

    public Assessment get(AssessmentId id) {
        return service.get(id);
    }

    public Assessment updateMetadata(AssessmentId id, AssessmentMetadata metadata) {
        return service.updateMetadata(id, metadata);
    }

    public Assessment start(AssessmentId id) {
        return service.start(id);
    }

    public Assessment complete(AssessmentId id) {
        return service.complete(id);
    }

    public Assessment archive(AssessmentId id) {
        return service.archive(id);
    }

    public List<Assessment> findAll() {
        return queryService.findAll();
    }

    public List<Assessment> findByType(AssessmentType type) {
        return queryService.findByType(type);
    }

    public List<Assessment> findByStatus(AssessmentStatus status) {
        return queryService.findByStatus(status);
    }

    public List<Assessment> findByTypeAndStatus(AssessmentType type, AssessmentStatus status) {
        return queryService.findByTypeAndStatus(type, status);
    }
}
