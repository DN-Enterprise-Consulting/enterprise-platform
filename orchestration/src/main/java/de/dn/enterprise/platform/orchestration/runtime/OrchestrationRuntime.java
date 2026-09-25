package de.dn.enterprise.platform.orchestration.runtime;

import de.dn.enterprise.platform.orchestration.application.OrchestrationApplication;
import de.dn.enterprise.platform.orchestration.persistence.FileOrchestrationPersistence;
import de.dn.enterprise.platform.orchestration.persistence.FileOrchestrationRepository;
import de.dn.enterprise.platform.orchestration.query.OrchestrationQueryService;
import de.dn.enterprise.platform.orchestration.service.PersistentOrchestrationService;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Runtime composition boundary for the orchestration module.
 *
 * <p>Composes the file-backed persistence adapter, repository, services and
 * stable application boundary without exposing infrastructure construction to callers.</p>
 */
public final class OrchestrationRuntime {

    private final PersistentOrchestrationService orchestrationService;
    private final OrchestrationApplication application;

    private OrchestrationRuntime(
            PersistentOrchestrationService orchestrationService,
            OrchestrationApplication application) {
        this.orchestrationService = orchestrationService;
        this.application = application;
    }

    public static OrchestrationRuntime fileBacked(Path storageDirectory) {
        Objects.requireNonNull(storageDirectory, "storageDirectory must not be null");

        FileOrchestrationPersistence persistence =
                new FileOrchestrationPersistence(storageDirectory);
        FileOrchestrationRepository repository =
                new FileOrchestrationRepository(persistence);
        PersistentOrchestrationService service =
                new PersistentOrchestrationService(repository);
        OrchestrationQueryService queryService =
                new OrchestrationQueryService(repository);

        return new OrchestrationRuntime(
                service,
                new OrchestrationApplication(service, queryService));
    }

    public OrchestrationApplication application() {
        return application;
    }

    public PersistentOrchestrationService orchestrationService() {
        return orchestrationService;
    }
}
