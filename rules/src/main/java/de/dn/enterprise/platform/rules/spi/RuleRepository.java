package de.dn.enterprise.platform.rules.spi;

import de.dn.enterprise.platform.rules.domain.Rule;
import de.dn.enterprise.platform.rules.domain.RuleId;
import de.dn.enterprise.platform.rules.domain.RuleStatus;
import de.dn.enterprise.platform.rules.domain.RuleType;

import java.util.List;
import java.util.Optional;

public interface RuleRepository {
    Rule save(Rule rule);
    Optional<Rule> findById(RuleId id);
    List<Rule> findAll();
    List<Rule> findByType(RuleType type);
    List<Rule> findByStatus(RuleStatus status);
    boolean existsById(RuleId id);
}
