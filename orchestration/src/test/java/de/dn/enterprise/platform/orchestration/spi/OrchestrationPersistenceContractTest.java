package de.dn.enterprise.platform.orchestration.spi;

import de.dn.enterprise.platform.orchestration.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OrchestrationPersistenceContractTest {

    private final OrchestrationPersistence persistence = new ContractTestPersistence();

    @Test
    void savesAndLoadsOrchestration() {
        var orchestration = sample();

        persistence.save(orchestration);

        assertThat(persistence.exists(orchestration.id())).isTrue();
        assertThat(persistence.load(orchestration.id())).isSameAs(orchestration);
    }

    @Test
    void reportsMissingOrchestration() {
        var id = OrchestrationId.newId();

        assertThat(persistence.exists(id)).isFalse();
        assertThat(persistence.load(id)).isNull();
    }

    @Test
    void deletesOrchestration() {
        var orchestration = sample();

        persistence.save(orchestration);
        persistence.delete(orchestration.id());

        assertThat(persistence.exists(orchestration.id())).isFalse();
        assertThat(persistence.load(orchestration.id())).isNull();
    }

    private static Orchestration sample() {
        return Orchestration.draft(
                OrchestrationType.FULL_ASSESSMENT,
                new OrchestrationMetadata("Test", "Persistence contract test", Map.of()),
                List.of(OrchestrationStep.pending(1, OrchestrationStepType.ASSESSMENT)));
    }

    private static final class ContractTestPersistence implements OrchestrationPersistence {

        private Orchestration stored;

        @Override
        public void save(Orchestration orchestration) {
            stored = orchestration;
        }

        @Override
        public Orchestration load(OrchestrationId id) {
            return stored != null && stored.id().equals(id) ? stored : null;
        }

        @Override
        public boolean exists(OrchestrationId id) {
            return stored != null && stored.id().equals(id);
        }

        @Override
        public void delete(OrchestrationId id) {
            if (stored != null && stored.id().equals(id)) {
                stored = null;
            }
        }
    }
}
