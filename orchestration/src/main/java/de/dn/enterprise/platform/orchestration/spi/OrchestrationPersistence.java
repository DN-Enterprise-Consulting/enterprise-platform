package de.dn.enterprise.platform.orchestration.spi;

import de.dn.enterprise.platform.orchestration.domain.Orchestration;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationId;

public interface OrchestrationPersistence {

    void save(Orchestration orchestration);

    Orchestration load(OrchestrationId id);

    boolean exists(OrchestrationId id);

    void delete(OrchestrationId id);
}
