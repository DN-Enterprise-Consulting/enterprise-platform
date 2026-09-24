package de.dn.enterprise.platform.knowledge.persistence;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.spi.KnowledgePersistence;
import de.dn.enterprise.platform.knowledge.spi.KnowledgeRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Repository adapter backed by the technology-neutral KnowledgePersistence SPI.
 */
public final class FileKnowledgeRepository implements KnowledgeRepository {

    private final KnowledgePersistence persistence;

    public FileKnowledgeRepository(KnowledgePersistence persistence) {
        this.persistence = Objects.requireNonNull(persistence, "persistence must not be null");
    }

    @Override
    public KnowledgeObject save(KnowledgeObject knowledgeObject) {
        return persistence.save(Objects.requireNonNull(knowledgeObject, "knowledgeObject must not be null"));
    }

    @Override
    public Optional<KnowledgeObject> findById(KnowledgeObjectId id) {
        return persistence.load(Objects.requireNonNull(id, "id must not be null"));
    }

    @Override
    public List<KnowledgeObject> findByType(KnowledgeObjectType type) {
        Objects.requireNonNull(type, "type must not be null");
        return findAll().stream()
                .filter(object -> object.type() == type)
                .toList();
    }

    @Override
    public boolean existsById(KnowledgeObjectId id) {
        return persistence.exists(Objects.requireNonNull(id, "id must not be null"));
    }

    @Override
    public List<KnowledgeObject> findAll() {
        if (persistence instanceof FileKnowledgePersistence filePersistence) {
            return filePersistence.loadAll();
        }
        throw new IllegalStateException(
                "The configured KnowledgePersistence does not expose a query operation");
    }
}
