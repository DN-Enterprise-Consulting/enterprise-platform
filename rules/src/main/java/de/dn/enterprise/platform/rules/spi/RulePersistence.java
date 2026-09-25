package de.dn.enterprise.platform.rules.spi;

import de.dn.enterprise.platform.rules.domain.Rule;
import de.dn.enterprise.platform.rules.domain.RuleId;

import java.util.Optional;

/**
 * Technology-neutral persistence contract for Rule aggregates.
 *
 * <p>The SPI deliberately contains no persistence technology concerns.
 * Implementations may use files, databases or other durable stores.</p>
 */
public interface RulePersistence {

    Rule save(Rule rule);

    Optional<Rule> load(RuleId id);

    boolean exists(RuleId id);

    void delete(RuleId id);
}
