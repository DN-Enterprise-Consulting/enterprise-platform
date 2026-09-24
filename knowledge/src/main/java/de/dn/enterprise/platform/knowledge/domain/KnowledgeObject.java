package de.dn.enterprise.platform.knowledge.domain;

import java.util.Objects;

public record KnowledgeObject(
        KnowledgeObjectId id,
        KnowledgeObjectType type,
        KnowledgeObjectVersion version,
        KnowledgeObjectStatus status,
        KnowledgeObjectMetadata metadata,
        String content) {

    public KnowledgeObject {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(version, "version must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(metadata, "metadata must not be null");
        Objects.requireNonNull(content, "content must not be null");
    }

    public static KnowledgeObject draft(
            KnowledgeObjectType type,
            KnowledgeObjectMetadata metadata,
            String content) {
        return new KnowledgeObject(
                KnowledgeObjectId.newId(),
                type,
                KnowledgeObjectVersion.initial(),
                KnowledgeObjectStatus.DRAFT,
                metadata,
                content);
    }

    public KnowledgeObject withStatus(KnowledgeObjectStatus newStatus) {
        return new KnowledgeObject(id, type, version, newStatus, metadata, content);
    }

    public KnowledgeObject withVersion(KnowledgeObjectVersion newVersion) {
        return new KnowledgeObject(id, type, newVersion, status, metadata, content);
    }
}
