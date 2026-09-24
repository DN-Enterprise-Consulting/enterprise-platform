package de.dn.enterprise.platform.knowledge.inmemory;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.spi.KnowledgeRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryKnowledgeRepository implements KnowledgeRepository {

    private final Map<KnowledgeObjectId, KnowledgeObject> objects = new ConcurrentHashMap<>();

    @Override
    public KnowledgeObject save(KnowledgeObject knowledgeObject) {
        Objects.requireNonNull(knowledgeObject, "knowledgeObject must not be null");
        objects.put(knowledgeObject.id(), knowledgeObject);
        return knowledgeObject;
    }

    @Override
    public Optional<KnowledgeObject> findById(KnowledgeObjectId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Optional.ofNullable(objects.get(id));
    }

    @Override
    public List<KnowledgeObject> findByType(KnowledgeObjectType type) {
        Objects.requireNonNull(type, "type must not be null");
        return objects.values().stream()
                .filter(object -> object.type() == type)
                .toList();
    }

    @Override
    public List<KnowledgeObject> findAll() {
        return List.copyOf(objects.values());
    }

    @Override
    public boolean existsById(KnowledgeObjectId id) {
        Objects.requireNonNull(id, "id must not be null");
        return objects.containsKey(id);
    }

    public int size() {
        return objects.size();
    }
}
