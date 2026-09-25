package de.dn.enterprise.platform.publishing.runtime;

import de.dn.enterprise.platform.publishing.application.PublicationApplication;
import de.dn.enterprise.platform.publishing.persistence.FilePublicationPersistence;
import de.dn.enterprise.platform.publishing.persistence.FilePublicationRepository;
import de.dn.enterprise.platform.publishing.service.PersistentPublicationService;

import java.nio.file.Path;
import java.util.Objects;

public final class PublicationRuntime {

    private final PersistentPublicationService publicationService;
    private final PublicationApplication application;

    private PublicationRuntime(
            PersistentPublicationService publicationService,
            PublicationApplication application) {
        this.publicationService = publicationService;
        this.application = application;
    }

    public static PublicationRuntime fileBacked(Path storageDirectory) {
        Objects.requireNonNull(
                storageDirectory, "storageDirectory must not be null");

        FilePublicationPersistence persistence =
                new FilePublicationPersistence(storageDirectory);
        FilePublicationRepository repository =
                new FilePublicationRepository(persistence);
        PersistentPublicationService service =
                new PersistentPublicationService(repository);

        return new PublicationRuntime(
                service,
                new PublicationApplication(service));
    }

    public PublicationApplication application() {
        return application;
    }

    public PersistentPublicationService publicationService() {
        return publicationService;
    }
}
