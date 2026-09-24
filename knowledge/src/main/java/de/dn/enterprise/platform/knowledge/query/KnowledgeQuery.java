package de.dn.enterprise.platform.knowledge.query;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectStatus;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;

import java.util.Optional;

public record KnowledgeQuery(
        Optional<KnowledgeObjectType> type,
        Optional<KnowledgeObjectStatus> status) {

    public KnowledgeQuery {
        type = type == null ? Optional.empty() : type;
        status = status == null ? Optional.empty() : status;
    }

    public static KnowledgeQuery all() {
        return new KnowledgeQuery(Optional.empty(), Optional.empty());
    }

    public static KnowledgeQuery byType(KnowledgeObjectType type) {
        return new KnowledgeQuery(Optional.of(type), Optional.empty());
    }

    public static KnowledgeQuery byStatus(KnowledgeObjectStatus status) {
        return new KnowledgeQuery(Optional.empty(), Optional.of(status));
    }

    public static KnowledgeQuery byTypeAndStatus(
            KnowledgeObjectType type,
            KnowledgeObjectStatus status) {
        return new KnowledgeQuery(Optional.of(type), Optional.of(status));
    }
}
