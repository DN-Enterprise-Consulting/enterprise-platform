package de.dn.enterprise.platform.knowledge.service;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectStatus;

public final class KnowledgeLifecycleException extends RuntimeException {

    public KnowledgeLifecycleException(
            KnowledgeObjectStatus currentStatus,
            KnowledgeObjectStatus requestedStatus) {
        super("Invalid knowledge object lifecycle transition: "
                + currentStatus + " -> " + requestedStatus);
    }
}
