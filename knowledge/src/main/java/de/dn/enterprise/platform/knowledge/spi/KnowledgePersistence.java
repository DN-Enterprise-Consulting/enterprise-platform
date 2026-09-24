package de.dn.enterprise.platform.knowledge.spi;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId;

import java.util.Optional;

/**
 * Persistence boundary for durable storage of KnowledgeObjects.
 *
 * <p>This SPI deliberately contains no database, transaction or framework
 * concepts. Implementations are responsible for translating the domain
 * object to and from their persistence technology.</p>
 */
public interface KnowledgePersistence {

    KnowledgeObject save(KnowledgeObject knowledgeObject);

    Optional<KnowledgeObject> load(KnowledgeObjectId id);

    boolean exists(KnowledgeObjectId id);

    void delete(KnowledgeObjectId id);
}
