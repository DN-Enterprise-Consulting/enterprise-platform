package de.dn.enterprise.platform.assessment.service;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentId;
import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import de.dn.enterprise.platform.assessment.spi.AssessmentRepository;
import de.dn.enterprise.platform.assessment.validation.AssessmentValidator;

import java.util.Objects;

/**
 * Application service that combines assessment validation, lifecycle handling
 * and repository persistence.
 */
public final class PersistentAssessmentService {

    private final AssessmentRepository repository;
    private final AssessmentLifecycleService lifecycleService;
    private final AssessmentValidator validator;

    public PersistentAssessmentService(AssessmentRepository repository) {
        this(repository, new AssessmentLifecycleService(), new AssessmentValidator());
    }

    public PersistentAssessmentService(
            AssessmentRepository repository,
            AssessmentLifecycleService lifecycleService,
            AssessmentValidator validator) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.lifecycleService = Objects.requireNonNull(lifecycleService, "lifecycleService must not be null");
        this.validator = Objects.requireNonNull(validator, "validator must not be null");
    }

    public Assessment create(AssessmentType type, AssessmentMetadata metadata) {
        Assessment assessment = Assessment.draft(type, metadata);
        return saveValidated(assessment);
    }

    public Assessment get(AssessmentId id) {
        Objects.requireNonNull(id, "id must not be null");
        return repository.findById(id)
                .orElseThrow(() -> new AssessmentNotFoundException(id));
    }

    public Assessment updateMetadata(AssessmentId id, AssessmentMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata must not be null");
        Assessment current = get(id);
        if (current.status() == AssessmentStatus.ARCHIVED) {
            throw new AssessmentLifecycleException(current.status(), AssessmentStatus.ARCHIVED);
        }
        return saveValidated(current.withMetadata(metadata));
    }

    public Assessment start(AssessmentId id) {
        return transition(id, AssessmentStatus.IN_PROGRESS);
    }

    public Assessment complete(AssessmentId id) {
        return transition(id, AssessmentStatus.COMPLETED);
    }

    public Assessment archive(AssessmentId id) {
        return transition(id, AssessmentStatus.ARCHIVED);
    }

    private Assessment transition(AssessmentId id, AssessmentStatus targetStatus) {
        Assessment current = get(id);
        Assessment transitioned = lifecycleService.transition(current, targetStatus);
        return saveValidated(transitioned);
    }

    private Assessment saveValidated(Assessment assessment) {
        validator.validate(assessment);
        return repository.save(assessment);
    }
}
