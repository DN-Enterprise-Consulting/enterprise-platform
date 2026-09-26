package de.dn.enterprise.platform.execution.spi;

import de.dn.enterprise.platform.execution.domain.ExecutionContext;
import de.dn.enterprise.platform.execution.domain.ExecutionResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExecutionAdapterSpiTest {

    @Test
    void adapterExposesStableDomainKeyAndExecutes() {
        ExecutionAdapter adapter = new StubAdapter("assessment");

        assertThat(adapter.domainKey()).isEqualTo("assessment");
        assertThat(adapter.execute(ExecutionContext.empty()).successful()).isTrue();
    }

    @Test
    void registryCanResolveAdapterByDomainKey() {
        ExecutionAdapter adapter = new StubAdapter("assessment");

        ExecutionAdapterRegistry registry = new StubRegistry(List.of(adapter));

        assertThat(registry.findByDomainKey("assessment")).contains(adapter);
        assertThat(registry.requireByDomainKey("assessment")).isSameAs(adapter);
    }

    @Test
    void registryRejectsUnknownDomain() {
        ExecutionAdapterRegistry registry =
                new StubRegistry(List.of(new StubAdapter("assessment")));

        assertThatThrownBy(() -> registry.requireByDomainKey("rules"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void registryRejectsNullDomainKey() {
        ExecutionAdapterRegistry registry =
                new StubRegistry(List.of(new StubAdapter("assessment")));

        assertThatThrownBy(() -> registry.requireByDomainKey(null))
                .isInstanceOf(NullPointerException.class);
    }

    private record StubAdapter(String domainKey) implements ExecutionAdapter {

        @Override
        public ExecutionResult execute(ExecutionContext context) {
            return ExecutionResult.success("stub executed");
        }
    }

    private record StubRegistry(List<ExecutionAdapter> adapters)
            implements ExecutionAdapterRegistry {

        @Override
        public Optional<ExecutionAdapter> findByDomainKey(String domainKey) {
            return adapters.stream()
                    .filter(adapter -> adapter.domainKey().equals(domainKey))
                    .findFirst();
        }

        @Override
        public List<ExecutionAdapter> all() {
            return List.copyOf(adapters);
        }
    }
}
