package de.dn.enterprise.platform.knowledge.service;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectMetadata;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectStatus;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.spi.KnowledgeRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class PersistentKnowledgeService {

    private final KnowledgeRepository repository;

    public PersistentKnowledgeService(KnowledgeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    public KnowledgeObject create(
            KnowledgeObjectType type,
            String name,
            String description,
            String content,
            Map<String, String> attributes) {

        KnowledgeObject object = KnowledgeObject.draft(
                type,
                new KnowledgeObjectMetadata(name, description, attributes),
                content);

        return repository.save(object);
    }

    public KnowledgeObject get(KnowledgeObjectId id) {
        return repository.findById(Objects.requireNonNull(id, "id must not be null"))
                .orElseThrow(() -> new KnowledgeObjectNotFoundException(id));
    }

    public List<KnowledgeObject> findByType(KnowledgeObjectType type) {
        return repository.findByType(Objects.requireNonNull(type, "type must not be null"));
    }

    public KnowledgeObject updateContent(KnowledgeObjectId id, String content) {
        KnowledgeObject current = get(id);
        if (current.status() == KnowledgeObjectStatus.ARCHIVED) {
            throw new KnowledgeLifecycleException(
                    current.status(), KnowledgeObjectStatus.ARCHIVED);
        }

        KnowledgeObject updated = new KnowledgeObject(
                current.id(),
                current.type(),
                current.version().nextPatch(),
                current.status(),
                current.metadata(),
                content);

        return repository.save(updated);
    }

    public KnowledgeObject activate(KnowledgeObjectId id) {
        KnowledgeObject current = get(id);
        KnowledgeObject updated = new KnowledgeObject(
                current.id(), current.type(), current.version(),
                KnowledgeObjectStatus.ACTIVE, current.metadata(), current.content());
        return repository.save(updated);
    }

    public KnowledgeObject deprecate(KnowledgeObjectId id) {
        KnowledgeObject current = get(id);
        KnowledgeObject updated = new KnowledgeObject(
                current.id(), current.type(), current.version(),
                KnowledgeObjectStatus.DEPRECATED, current.metadata(), current.content());
        return repository.save(updated);
    }

    public KnowledgeObject archive(KnowledgeObjectId id) {
        KnowledgeObject current = get(id);
        KnowledgeObject updated = new KnowledgeObject(
                current.id(), current.type(), current.version(),
                KnowledgeObjectStatus.ARCHIVED, current.metadata(), current.content());
        return repository.save(updated);
    }
}
