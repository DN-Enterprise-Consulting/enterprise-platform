package de.dn.enterprise.platform.knowledge.service;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectMetadata;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.spi.KnowledgeRepository;
import de.dn.enterprise.platform.knowledge.validation.KnowledgeObjectValidator;

import java.util.List;
import java.util.Objects;

public final class KnowledgeService {

    private final KnowledgeRepository repository;
    private final KnowledgeLifecycleService lifecycleService;
    private final KnowledgeObjectValidator validator;

    public KnowledgeService(KnowledgeRepository repository) {
        this(repository, new KnowledgeLifecycleService(), new KnowledgeObjectValidator());
    }

    public KnowledgeService(
            KnowledgeRepository repository,
            KnowledgeLifecycleService lifecycleService,
            KnowledgeObjectValidator validator) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.lifecycleService = Objects.requireNonNull(
                lifecycleService, "lifecycleService must not be null");
        this.validator = Objects.requireNonNull(
                validator, "validator must not be null");
    }

    public KnowledgeObject create(
            KnowledgeObjectType type,
            KnowledgeObjectMetadata metadata,
            String content) {
        var object = KnowledgeObject.draft(type, metadata, content);
        validator.validate(object);
        return repository.save(object);
    }

    public KnowledgeObject get(KnowledgeObjectId id) {
        return repository.findById(id)
                .orElseThrow(() -> new KnowledgeObjectNotFoundException(id));
    }

    public List<KnowledgeObject> findByType(KnowledgeObjectType type) {
        return repository.findByType(type);
    }

    public KnowledgeObject updateContent(
            KnowledgeObjectId id,
            KnowledgeObjectMetadata metadata,
            String content) {
        var current = get(id);
        lifecycleService.requireUpdatable(current);

        var updated = new KnowledgeObject(
                current.id(),
                current.type(),
                current.version().nextPatch(),
                current.status(),
                metadata,
                content);

        validator.validate(updated);
        return repository.save(updated);
    }

    public KnowledgeObject activate(KnowledgeObjectId id) {
        var current = get(id);
        return repository.save(lifecycleService.activate(current));
    }

    public KnowledgeObject deprecate(KnowledgeObjectId id) {
        var current = get(id);
        return repository.save(lifecycleService.deprecate(current));
    }

    public KnowledgeObject archive(KnowledgeObjectId id) {
        var current = get(id);
        return repository.save(lifecycleService.archive(current));
    }
}
