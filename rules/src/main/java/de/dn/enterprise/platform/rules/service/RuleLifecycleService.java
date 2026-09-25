package de.dn.enterprise.platform.rules.service;

import de.dn.enterprise.platform.rules.domain.Rule;
import de.dn.enterprise.platform.rules.domain.RuleStatus;

import java.util.Objects;

public final class RuleLifecycleService {

    public Rule transition(Rule rule, RuleStatus target) {
        Objects.requireNonNull(rule, "rule must not be null");
        Objects.requireNonNull(target, "target must not be null");

        if (rule.status() == target) {
            return rule;
        }

        if (!isAllowed(rule.status(), target)) {
            throw new RuleLifecycleException(rule.status(), target);
        }

        return new Rule(
                rule.id(),
                rule.type(),
                target,
                rule.metadata(),
                rule.definition());
    }

    private boolean isAllowed(RuleStatus current, RuleStatus target) {
        return (current == RuleStatus.DRAFT && target == RuleStatus.ACTIVE)
                || (current == RuleStatus.ACTIVE && target == RuleStatus.DEPRECATED)
                || (current == RuleStatus.DEPRECATED && target == RuleStatus.ARCHIVED);
    }
}
