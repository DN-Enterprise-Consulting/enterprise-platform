package de.dn.enterprise.platform.orchestration.service;

import de.dn.enterprise.platform.orchestration.domain.OrchestrationStatus;

public final class OrchestrationLifecycleException extends RuntimeException {
    public OrchestrationLifecycleException(OrchestrationStatus from, OrchestrationStatus to) {
        super("Invalid orchestration lifecycle transition: " + from + " -> " + to);
    }
}
