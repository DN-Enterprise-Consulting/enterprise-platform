package de.dn.enterprise.platform.execution.registry;

import de.dn.enterprise.platform.execution.spi.ExecutionAdapter;
import de.dn.enterprise.platform.execution.spi.ExecutionAdapterRegistry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe registry for execution adapters keyed by their domain key.
 */
public final class InMemoryExecutionAdapterRegistry implements ExecutionAdapterRegistry {

    private final Map<String, ExecutionAdapter> adapters = new ConcurrentHashMap<>();

    public InMemoryExecutionAdapterRegistry() {
    }

    public InMemoryExecutionAdapterRegistry(List<? extends ExecutionAdapter> adapters) {
        Objects.requireNonNull(adapters, "adapters must not be null");
        adapters.forEach(this::register);
    }

    public void register(ExecutionAdapter adapter) {
        Objects.requireNonNull(adapter, "adapter must not be null");
        String domainKey = Objects.requireNonNull(adapter.domainKey(), "adapter.domainKey must not be null");
        if (domainKey.isBlank()) {
            throw new IllegalArgumentException("adapter.domainKey must not be blank");
        }
        ExecutionAdapter previous = adapters.putIfAbsent(domainKey, adapter);
        if (previous != null) {
            throw new IllegalArgumentException("Execution adapter already registered for domain: " + domainKey);
        }
    }

    public void unregister(String domainKey) {
        Objects.requireNonNull(domainKey, "domainKey must not be null");
        adapters.remove(domainKey);
    }

    @Override
    public Optional<ExecutionAdapter> findByDomainKey(String domainKey) {
        Objects.requireNonNull(domainKey, "domainKey must not be null");
        return Optional.ofNullable(adapters.get(domainKey));
    }

    @Override
    public List<ExecutionAdapter> all() {
        return adapters.values().stream()
                .sorted(Comparator.comparing(ExecutionAdapter::domainKey))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
