package de.dn.enterprise.platform.orchestration.spi;

import de.dn.enterprise.platform.orchestration.domain.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

class OrchestrationRepositoryContractTest {
    private final OrchestrationRepository repository = new ContractTestRepository();

    @Test void savesAndFindsById() {
        var o = sample();
        assertThat(repository.save(o)).isSameAs(o);
        assertThat(repository.findById(o.id())).containsSame(o);
    }

    @Test void returnsAll() {
        var o = sample();
        repository.save(o);
        assertThat(repository.findAll()).containsExactly(o);
    }

    @Test void findsByType() {
        var o = sample();
        repository.save(o);
        assertThat(repository.findByType(OrchestrationType.FULL_ASSESSMENT)).containsExactly(o);
    }

    @Test void findsByStatus() {
        var o = sample();
        repository.save(o);
        assertThat(repository.findByStatus(OrchestrationStatus.DRAFT)).containsExactly(o);
    }

    @Test void checksExistence() {
        var o = sample();
        assertThat(repository.existsById(o.id())).isFalse();
        repository.save(o);
        assertThat(repository.existsById(o.id())).isTrue();
    }

    private static Orchestration sample() {
        return Orchestration.draft(
            OrchestrationType.FULL_ASSESSMENT,
            new OrchestrationMetadata("Test", "Contract test", Map.of()),
            List.of(OrchestrationStep.pending(1, OrchestrationStepType.ASSESSMENT)));
    }

    private static final class ContractTestRepository implements OrchestrationRepository {
        private Orchestration stored;

        public Orchestration save(Orchestration o) { stored = o; return o; }
        public Optional<Orchestration> findById(OrchestrationId id) {
            return stored != null && stored.id().equals(id) ? Optional.of(stored) : Optional.empty();
        }
        public List<Orchestration> findAll() { return stored == null ? List.of() : List.of(stored); }
        public List<Orchestration> findByType(OrchestrationType type) {
            return stored != null && stored.type() == type ? List.of(stored) : List.of();
        }
        public List<Orchestration> findByStatus(OrchestrationStatus status) {
            return stored != null && stored.status() == status ? List.of(stored) : List.of();
        }
        public boolean existsById(OrchestrationId id) {
            return stored != null && stored.id().equals(id);
        }
    }
}
