package de.dn.enterprise.platform.identity.runtime;

import de.dn.enterprise.platform.identity.application.IdentityApplication;
import de.dn.enterprise.platform.identity.persistence.FileIdentityPersistence;
import de.dn.enterprise.platform.identity.persistence.FileIdentityRepository;
import de.dn.enterprise.platform.identity.query.IdentityQueryService;
import de.dn.enterprise.platform.identity.service.PersistentIdentityService;
import de.dn.enterprise.platform.identity.validation.IdentityValidator;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Runtime composition boundary for the identity module.
 *
 * <p>Composes file-backed persistence, repository, validation, query and
 * application services without exposing infrastructure construction to callers.</p>
 */
public final class IdentityRuntime {

    private final FileIdentityPersistence persistence;
    private final FileIdentityRepository repository;
    private final IdentityValidator validator;
    private final PersistentIdentityService identityService;
    private final IdentityQueryService queryService;
    private final IdentityApplication application;

    private IdentityRuntime(
            FileIdentityPersistence persistence,
            FileIdentityRepository repository,
            IdentityValidator validator,
            PersistentIdentityService identityService,
            IdentityQueryService queryService,
            IdentityApplication application) {
        this.persistence = Objects.requireNonNull(persistence, "persistence must not be null");
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.validator = Objects.requireNonNull(validator, "validator must not be null");
        this.identityService = Objects.requireNonNull(identityService, "identityService must not be null");
        this.queryService = Objects.requireNonNull(queryService, "queryService must not be null");
        this.application = Objects.requireNonNull(application, "application must not be null");
    }

    public static IdentityRuntime fileBacked(Path storageDirectory) {
        Objects.requireNonNull(storageDirectory, "storageDirectory must not be null");

        FileIdentityPersistence persistence = new FileIdentityPersistence(storageDirectory);
        FileIdentityRepository repository = new FileIdentityRepository(persistence);
        IdentityValidator validator = new IdentityValidator();
        PersistentIdentityService identityService =
                new PersistentIdentityService(repository, validator);

        IdentityQueryService queryService = new IdentityQueryService(repository);
        IdentityApplication application = new IdentityApplication(identityService, queryService);

        return new IdentityRuntime(
                persistence,
                repository,
                validator,
                identityService,
                queryService,
                application);
    }

    public FileIdentityPersistence persistence() {
        return persistence;
    }

    public FileIdentityRepository repository() {
        return repository;
    }

    public IdentityValidator validator() {
        return validator;
    }

    public PersistentIdentityService identityService() {
        return identityService;
    }

    public IdentityQueryService queryService() {
        return queryService;
    }

    public IdentityApplication application() {
        return application;
    }
}
