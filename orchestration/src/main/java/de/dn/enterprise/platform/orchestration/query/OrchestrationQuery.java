package de.dn.enterprise.platform.orchestration.query;

import de.dn.enterprise.platform.orchestration.domain.OrchestrationStatus;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationType;

import java.util.Optional;

/**
 * Optional filters for orchestration retrieval.
 * Empty filters mean that the corresponding dimension is unrestricted.
 */
public record OrchestrationQuery(
        Optional<OrchestrationType> type,
        Optional<OrchestrationStatus> status) {

    public OrchestrationQuery {
        type = type == null ? Optional.empty() : type;
        status = status == null ? Optional.empty() : status;
    }

    public static OrchestrationQuery all() {
        return new OrchestrationQuery(Optional.empty(), Optional.empty());
    }

    public static OrchestrationQuery byType(OrchestrationType type) {
        return new OrchestrationQuery(Optional.of(type), Optional.empty());
    }

    public static OrchestrationQuery byStatus(OrchestrationStatus status) {
        return new OrchestrationQuery(Optional.empty(), Optional.of(status));
    }

    public static OrchestrationQuery byTypeAndStatus(OrchestrationType type, OrchestrationStatus status) {
        return new OrchestrationQuery(Optional.of(type), Optional.of(status));
    }
}
