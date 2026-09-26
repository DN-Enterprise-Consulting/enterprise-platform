package de.dn.enterprise.platform.execution.spi;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface ExecutionAdapterRegistry {

    Optional<ExecutionAdapter> findByDomainKey(String domainKey);

    default ExecutionAdapter requireByDomainKey(String domainKey) {
        Objects.requireNonNull(domainKey, "domainKey must not be null");
        return findByDomainKey(domainKey)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No execution adapter registered for domain: " + domainKey));
    }

    List<ExecutionAdapter> all();
}
