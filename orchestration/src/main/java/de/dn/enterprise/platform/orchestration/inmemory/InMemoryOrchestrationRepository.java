package de.dn.enterprise.platform.orchestration.inmemory;

import de.dn.enterprise.platform.orchestration.domain.Orchestration;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationId;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStatus;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationType;
import de.dn.enterprise.platform.orchestration.spi.OrchestrationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class InMemoryOrchestrationRepository implements OrchestrationRepository {

    private final ConcurrentMap<OrchestrationId, Orchestration> store = new ConcurrentHashMap<>();

    @Override
    public Orchestration save(Orchestration orchestration) {
        Objects.requireNonNull(orchestration, "orchestration must not be null");
        store.put(orchestration.id(), orchestration);
        return orchestration;
    }

    @Override
    public Optional<Orchestration> findById(OrchestrationId id) {
        Objects.requireNonNull(id, "id must not be null");
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Orchestration> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public List<Orchestration> findByType(OrchestrationType type) {
        Objects.requireNonNull(type, "type must not be null");
        return store.values().stream()
                .filter(orchestration -> orchestration.type() == type)
                .toList();
    }

    @Override
    public List<Orchestration> findByStatus(OrchestrationStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return store.values().stream()
                .filter(orchestration -> orchestration.status() == status)
                .toList();
    }

    @Override
    public boolean existsById(OrchestrationId id) {
        Objects.requireNonNull(id, "id must not be null");
        return store.containsKey(id);
    }
}
