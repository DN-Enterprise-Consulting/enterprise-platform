package de.dn.enterprise.platform.orchestration.spi;

import de.dn.enterprise.platform.orchestration.domain.Orchestration;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationId;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStatus;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationType;
import java.util.List;
import java.util.Optional;

public interface OrchestrationRepository {
    Orchestration save(Orchestration orchestration);
    Optional<Orchestration> findById(OrchestrationId id);
    List<Orchestration> findAll();
    List<Orchestration> findByType(OrchestrationType type);
    List<Orchestration> findByStatus(OrchestrationStatus status);
    boolean existsById(OrchestrationId id);
}
