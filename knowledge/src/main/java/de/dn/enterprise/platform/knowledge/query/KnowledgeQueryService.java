package de.dn.enterprise.platform.knowledge.query;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.spi.KnowledgeRepository;

import java.util.List;
import java.util.Objects;

public final class KnowledgeQueryService {

    private final KnowledgeRepository repository;

    public KnowledgeQueryService(KnowledgeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    public List<KnowledgeObject> find(KnowledgeQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return repository.findAll().stream()
                .filter(object -> query.type()
                        .map(type -> object.type() == type)
                        .orElse(true))
                .filter(object -> query.status()
                        .map(status -> object.status() == status)
                        .orElse(true))
                .toList();
    }
}
