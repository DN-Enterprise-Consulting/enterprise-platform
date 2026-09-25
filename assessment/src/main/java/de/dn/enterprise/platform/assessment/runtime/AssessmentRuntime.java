package de.dn.enterprise.platform.assessment.runtime;

import de.dn.enterprise.platform.assessment.api.AssessmentApplication;
import de.dn.enterprise.platform.assessment.persistence.FileAssessmentPersistence;
import de.dn.enterprise.platform.assessment.persistence.FileAssessmentRepository;
import de.dn.enterprise.platform.assessment.query.AssessmentQueryService;
import de.dn.enterprise.platform.assessment.service.AssessmentLifecycleService;
import de.dn.enterprise.platform.assessment.service.PersistentAssessmentService;
import de.dn.enterprise.platform.assessment.spi.AssessmentPersistence;
import de.dn.enterprise.platform.assessment.spi.AssessmentRepository;
import de.dn.enterprise.platform.assessment.validation.AssessmentValidator;

import java.util.Objects;

public final class AssessmentRuntime {

    private final AssessmentRepository repository;
    private final AssessmentQueryService queryService;
    private final AssessmentValidator validator;
    private final PersistentAssessmentService service;
    private final AssessmentApplication application;

    private AssessmentRuntime(
            AssessmentRepository repository,
            AssessmentQueryService queryService,
            AssessmentValidator validator,
            PersistentAssessmentService service,
            AssessmentApplication application) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.queryService = Objects.requireNonNull(queryService, "queryService must not be null");
        this.validator = Objects.requireNonNull(validator, "validator must not be null");
        this.service = Objects.requireNonNull(service, "service must not be null");
        this.application = Objects.requireNonNull(application, "application must not be null");
    }

    public static AssessmentRuntime create(AssessmentRuntimeConfiguration configuration) {
        Objects.requireNonNull(configuration, "configuration must not be null");

        AssessmentPersistence persistence =
                new FileAssessmentPersistence(configuration.storageDirectory());
        AssessmentRepository repository =
                new FileAssessmentRepository(persistence);

        AssessmentValidator validator = new AssessmentValidator();
        AssessmentQueryService queryService = new AssessmentQueryService(repository);
        PersistentAssessmentService service =
                new PersistentAssessmentService(repository, new AssessmentLifecycleService(), validator);
        AssessmentApplication application = new AssessmentApplication(service, queryService);

        return new AssessmentRuntime(repository, queryService, validator, service, application);
    }

    /**
     * Stable application-facing API for Assessment use cases.
     */
    public AssessmentApplication application() {
        return application;
    }

    /**
     * Transitional infrastructure accessors retained for internal composition
     * and backward compatibility. New consumers should use {@link #application()}.
     */
    public AssessmentRepository repository() {
        return repository;
    }

    public AssessmentQueryService queryService() {
        return queryService;
    }

    public AssessmentValidator validator() {
        return validator;
    }

    public PersistentAssessmentService service() {
        return service;
    }
}
