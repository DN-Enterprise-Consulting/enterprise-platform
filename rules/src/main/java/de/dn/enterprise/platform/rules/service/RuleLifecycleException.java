package de.dn.enterprise.platform.rules.service;

import de.dn.enterprise.platform.rules.domain.RuleStatus;

public final class RuleLifecycleException extends RuntimeException {
    public RuleLifecycleException(RuleStatus current, RuleStatus target) {
        super("Invalid rule lifecycle transition: " + current + " -> " + target);
    }
}
