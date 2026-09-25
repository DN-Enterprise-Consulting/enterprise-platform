package de.dn.enterprise.platform.rules.inmemory;

import de.dn.enterprise.platform.rules.domain.Rule;
import de.dn.enterprise.platform.rules.domain.RuleId;
import de.dn.enterprise.platform.rules.domain.RuleStatus;
import de.dn.enterprise.platform.rules.domain.RuleType;
import de.dn.enterprise.platform.rules.spi.RuleRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe in-memory implementation of the RuleRepository SPI.
 */
public final class InMemoryRuleRepository implements RuleRepository {

    private final ConcurrentMap<RuleId, Rule> rules = new ConcurrentHashMap<>();

    @Override
    public Rule save(Rule rule) {
        Objects.requireNonNull(rule, "rule must not be null");
        rules.put(rule.id(), rule);
        return rule;
    }

    @Override
    public Optional<Rule> findById(RuleId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Optional.ofNullable(rules.get(id));
    }

    @Override
    public List<Rule> findAll() {
        return List.copyOf(rules.values());
    }

    @Override
    public List<Rule> findByType(RuleType type) {
        Objects.requireNonNull(type, "type must not be null");
        return rules.values().stream()
                .filter(rule -> rule.type() == type)
                .toList();
    }

    @Override
    public List<Rule> findByStatus(RuleStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return rules.values().stream()
                .filter(rule -> rule.status() == status)
                .toList();
    }

    @Override
    public boolean existsById(RuleId id) {
        Objects.requireNonNull(id, "id must not be null");
        return rules.containsKey(id);
    }

    public int size() {
        return rules.size();
    }
}
