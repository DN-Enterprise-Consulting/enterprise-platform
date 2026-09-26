package de.dn.enterprise.platform.execution.registry;

import de.dn.enterprise.platform.execution.domain.ExecutionContext;
import de.dn.enterprise.platform.execution.domain.ExecutionResult;
import de.dn.enterprise.platform.execution.spi.ExecutionAdapter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryExecutionAdapterRegistryTest {

    @Test
    void registersAndResolvesAdapterByDomainKey() {
        ExecutionAdapter assessment = adapter("assessment");
        InMemoryExecutionAdapterRegistry registry = new InMemoryExecutionAdapterRegistry(List.of(assessment));

        assertThat(registry.findByDomainKey("assessment")).containsSame(assessment);
        assertThat(registry.requireByDomainKey("assessment")).isSameAs(assessment);
        assertThat(registry.all()).containsExactly(assessment);
    }

    @Test
    void rejectsDuplicateDomainKey() {
        InMemoryExecutionAdapterRegistry registry = new InMemoryExecutionAdapterRegistry();
        registry.register(adapter("assessment"));

        assertThatThrownBy(() -> registry.register(adapter("assessment")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    void returnsDeterministicDomainOrder() {
        InMemoryExecutionAdapterRegistry registry = new InMemoryExecutionAdapterRegistry(
                List.of(adapter("publishing"), adapter("assessment"), adapter("knowledge")));

        assertThat(registry.all())
                .extracting(ExecutionAdapter::domainKey)
                .containsExactly("assessment", "knowledge", "publishing");
    }

    @Test
    void missingDomainReturnsEmptyAndRequireThrows() {
        InMemoryExecutionAdapterRegistry registry = new InMemoryExecutionAdapterRegistry();

        assertThat(registry.findByDomainKey("rules")).isEmpty();
        assertThatThrownBy(() -> registry.requireByDomainKey("rules"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("rules");
    }

    private ExecutionAdapter adapter(String domainKey) {
        return new ExecutionAdapter() {
            @Override
            public String domainKey() {
                return domainKey;
            }

            @Override
            public ExecutionResult execute(ExecutionContext context) {
                return ExecutionResult.success("test", Map.of("domain", domainKey));
            }
        };
    }
}
