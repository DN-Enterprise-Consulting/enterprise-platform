package de.dn.enterprise.platform.knowledge.service;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId;

public final class KnowledgeObjectNotFoundException extends RuntimeException {

    public KnowledgeObjectNotFoundException(KnowledgeObjectId id) {
        super("Knowledge object not found: " + id);
    }
}
