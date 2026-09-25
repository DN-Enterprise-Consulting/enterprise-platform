package de.dn.enterprise.platform.rules.service;

import de.dn.enterprise.platform.rules.domain.RuleId;

public final class RuleNotFoundException extends RuntimeException {
    public RuleNotFoundException(RuleId id) {
        super("Rule not found: " + id);
    }
}
