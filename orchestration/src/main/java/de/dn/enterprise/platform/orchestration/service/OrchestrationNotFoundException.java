package de.dn.enterprise.platform.orchestration.service;

import de.dn.enterprise.platform.orchestration.domain.OrchestrationId;

public final class OrchestrationNotFoundException extends RuntimeException {
    public OrchestrationNotFoundException(OrchestrationId id) {
        super("Orchestration not found: " + id.value());
    }
}
