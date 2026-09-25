package de.dn.enterprise.platform.rules.service;

import de.dn.enterprise.platform.rules.domain.Rule;
import de.dn.enterprise.platform.rules.domain.RuleId;
import de.dn.enterprise.platform.rules.domain.RuleMetadata;
import de.dn.enterprise.platform.rules.domain.RuleStatus;
import de.dn.enterprise.platform.rules.domain.RuleType;
import de.dn.enterprise.platform.rules.spi.RuleRepository;
import de.dn.enterprise.platform.rules.validation.RuleValidator;

import java.util.List;
import java.util.Objects;

public final class PersistentRuleService {

    private final RuleRepository repository;
    private final RuleValidator validator;
    private final RuleLifecycleService lifecycle;

    public PersistentRuleService(RuleRepository repository) {
        this(repository, new RuleValidator(), new RuleLifecycleService());
    }

    public PersistentRuleService(
            RuleRepository repository,
            RuleValidator validator,
            RuleLifecycleService lifecycle) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.validator = Objects.requireNonNull(validator, "validator must not be null");
        this.lifecycle = Objects.requireNonNull(lifecycle, "lifecycle must not be null");
    }

    public Rule create(RuleType type, RuleMetadata metadata, String definition) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(metadata, "metadata must not be null");
        Objects.requireNonNull(definition, "definition must not be null");

        Rule rule = new Rule(
                RuleId.newId(),
                type,
                RuleStatus.DRAFT,
                metadata,
                definition);

        validator.validate(rule);
        return repository.save(rule);
    }

    public Rule get(RuleId id) {
        Objects.requireNonNull(id, "id must not be null");
        return repository.findById(id)
                .orElseThrow(() -> new RuleNotFoundException(id));
    }

    public List<Rule> findAll() {
        return repository.findAll();
    }

    public List<Rule> findByType(RuleType type) {
        return repository.findByType(Objects.requireNonNull(type, "type must not be null"));
    }

    public List<Rule> findByStatus(RuleStatus status) {
        return repository.findByStatus(Objects.requireNonNull(status, "status must not be null"));
    }

    public Rule updateDefinition(RuleId id, String definition) {
        Objects.requireNonNull(definition, "definition must not be null");

        Rule current = get(id);
        ensureMutable(current);

        Rule updated = new Rule(
                current.id(),
                current.type(),
                current.status(),
                current.metadata(),
                definition);

        validator.validate(updated);
        return repository.save(updated);
    }

    public Rule updateMetadata(RuleId id, RuleMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata must not be null");

        Rule current = get(id);
        ensureMutable(current);

        Rule updated = new Rule(
                current.id(),
                current.type(),
                current.status(),
                metadata,
                current.definition());

        validator.validate(updated);
        return repository.save(updated);
    }

    public Rule activate(RuleId id) {
        return transition(id, RuleStatus.ACTIVE);
    }

    public Rule deprecate(RuleId id) {
        return transition(id, RuleStatus.DEPRECATED);
    }

    public Rule archive(RuleId id) {
        return transition(id, RuleStatus.ARCHIVED);
    }

    private Rule transition(RuleId id, RuleStatus target) {
        Rule current = get(id);
        Rule updated = lifecycle.transition(current, target);
        validator.validate(updated);
        return repository.save(updated);
    }

    private void ensureMutable(Rule rule) {
        if (rule.status() == RuleStatus.ARCHIVED) {
            throw new RuleLifecycleException(rule.status(), rule.status());
        }
    }
}
