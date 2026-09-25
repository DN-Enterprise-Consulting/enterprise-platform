package de.dn.enterprise.platform.rules.validation;

import de.dn.enterprise.platform.rules.domain.Rule;

import java.util.Objects;

public final class RuleValidator {

    public void validate(Rule rule) {
        if (rule == null) {
            throw new RuleValidationException("rule must not be null");
        }
        if (rule.id() == null) {
            throw new RuleValidationException("rule.id must not be null");
        }
        if (rule.type() == null) {
            throw new RuleValidationException("rule.type must not be null");
        }
        if (rule.status() == null) {
            throw new RuleValidationException("rule.status must not be null");
        }
        if (rule.metadata() == null) {
            throw new RuleValidationException("rule.metadata must not be null");
        }
        if (rule.metadata().name() == null || rule.metadata().name().isBlank()) {
            throw new RuleValidationException("rule.metadata.name must not be blank");
        }
        if (rule.definition() == null || rule.definition().isBlank()) {
            throw new RuleValidationException("rule.definition must not be blank");
        }
    }
}
