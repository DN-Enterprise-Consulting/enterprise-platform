package de.dn.enterprise.platform.bootstrap;

import de.dn.enterprise.platform.knowledge.persistence.FileKnowledgePersistence;
import de.dn.enterprise.platform.knowledge.persistence.FileKnowledgeRepository;
import de.dn.enterprise.platform.knowledge.service.PersistentKnowledgeService;

import java.util.Objects;

/**
 * Runtime composition root for the Knowledge subsystem.
 */
public final class KnowledgeRuntime {

    private final PersistentKnowledgeService knowledgeService;

    private KnowledgeRuntime(PersistentKnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    public static KnowledgeRuntime create(KnowledgeRuntimeConfiguration configuration) {
        Objects.requireNonNull(configuration, "configuration must not be null");

        FileKnowledgePersistence persistence =
                new FileKnowledgePersistence(configuration.storageDirectory());
        FileKnowledgeRepository repository =
                new FileKnowledgeRepository(persistence);

        return new KnowledgeRuntime(
                new PersistentKnowledgeService(repository));
    }

    public PersistentKnowledgeService knowledgeService() {
        return knowledgeService;
    }
}
