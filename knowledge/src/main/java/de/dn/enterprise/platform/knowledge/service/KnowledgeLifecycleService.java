package de.dn.enterprise.platform.knowledge.service;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectStatus;

import java.util.Objects;

public final class KnowledgeLifecycleService {

    public KnowledgeObject activate(KnowledgeObject object) {
        Objects.requireNonNull(object, "object must not be null");
        requireTransition(object.status(), KnowledgeObjectStatus.ACTIVE);
        return object.withStatus(KnowledgeObjectStatus.ACTIVE);
    }

    public KnowledgeObject deprecate(KnowledgeObject object) {
        Objects.requireNonNull(object, "object must not be null");
        requireTransition(object.status(), KnowledgeObjectStatus.DEPRECATED);
        return object.withStatus(KnowledgeObjectStatus.DEPRECATED);
    }

    public KnowledgeObject archive(KnowledgeObject object) {
        Objects.requireNonNull(object, "object must not be null");
        requireTransition(object.status(), KnowledgeObjectStatus.ARCHIVED);
        return object.withStatus(KnowledgeObjectStatus.ARCHIVED);
    }

    public void requireUpdatable(KnowledgeObject object) {
        Objects.requireNonNull(object, "object must not be null");
        if (object.status() == KnowledgeObjectStatus.ARCHIVED) {
            throw new KnowledgeLifecycleException(
                    object.status(), object.status());
        }
    }

    private void requireTransition(
            KnowledgeObjectStatus current,
            KnowledgeObjectStatus target) {

        boolean allowed = switch (current) {
            case DRAFT -> target == KnowledgeObjectStatus.ACTIVE;
            case ACTIVE -> target == KnowledgeObjectStatus.DEPRECATED;
            case DEPRECATED -> target == KnowledgeObjectStatus.ARCHIVED;
            case ARCHIVED -> false;
        };

        if (!allowed) {
            throw new KnowledgeLifecycleException(current, target);
        }
    }
}
