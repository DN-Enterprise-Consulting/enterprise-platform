package de.dn.enterprise.platform.rules.persistence;

import de.dn.enterprise.platform.rules.domain.Rule;
import de.dn.enterprise.platform.rules.domain.RuleId;
import de.dn.enterprise.platform.rules.domain.RuleStatus;
import de.dn.enterprise.platform.rules.domain.RuleType;
import de.dn.enterprise.platform.rules.spi.RulePersistence;
import de.dn.enterprise.platform.rules.spi.RuleRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class FileRuleRepository implements RuleRepository {

    private final RulePersistence persistence;

    public FileRuleRepository(RulePersistence persistence) {
        this.persistence = Objects.requireNonNull(persistence, "persistence must not be null");
    }

    @Override
    public Rule save(Rule rule) {
        return persistence.save(Objects.requireNonNull(rule, "rule must not be null"));
    }

    @Override
    public Optional<Rule> findById(RuleId id) {
        return persistence.load(Objects.requireNonNull(id, "id must not be null"));
    }

    @Override
    public List<Rule> findAll() {
        if (!(persistence instanceof FileRulePersistence filePersistence)) {
            throw new IllegalStateException(
                    "findAll requires FileRulePersistence until RulePersistence exposes loadAll()");
        }
        return filePersistence.loadAll();
    }

    @Override
    public List<Rule> findByType(RuleType type) {
        Objects.requireNonNull(type, "type must not be null");
        return findAll().stream()
                .filter(rule -> rule.type() == type)
                .toList();
    }

    @Override
    public List<Rule> findByStatus(RuleStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return findAll().stream()
                .filter(rule -> rule.status() == status)
                .toList();
    }

    @Override
    public boolean existsById(RuleId id) {
        return persistence.exists(Objects.requireNonNull(id, "id must not be null"));
    }
}
