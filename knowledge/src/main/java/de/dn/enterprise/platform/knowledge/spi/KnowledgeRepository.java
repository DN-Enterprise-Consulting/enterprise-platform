package de.dn.enterprise.platform.knowledge.spi;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;

import java.util.List;
import java.util.Optional;

public interface KnowledgeRepository {

    KnowledgeObject save(KnowledgeObject knowledgeObject);

    Optional<KnowledgeObject> findById(KnowledgeObjectId id);

    List<KnowledgeObject> findByType(KnowledgeObjectType type);

    List<KnowledgeObject> findAll();

    boolean existsById(KnowledgeObjectId id);
}
