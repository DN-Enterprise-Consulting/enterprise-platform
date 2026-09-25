package de.dn.enterprise.platform.orchestration.inmemory;

import de.dn.enterprise.platform.orchestration.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryOrchestrationRepositoryTest {

    private final InMemoryOrchestrationRepository repository = new InMemoryOrchestrationRepository();

    @Test
    void savesAndFindsById() {
        var orchestration = draft(OrchestrationType.FULL_ASSESSMENT);
        assertThat(repository.save(orchestration)).isSameAs(orchestration);
        assertThat(repository.findById(orchestration.id())).containsSame(orchestration);
    }

    @Test
    void replacesExistingOrchestrationWithSameId() {
        var original = draft(OrchestrationType.FULL_ASSESSMENT);
        var replacement = new Orchestration(
                original.id(),
                OrchestrationType.ASSESSMENT_TO_KNOWLEDGE,
                OrchestrationStatus.DRAFT,
                original.metadata(),
                original.steps());

        repository.save(original);
        repository.save(replacement);

        assertThat(repository.findById(original.id())).containsSame(replacement);
        assertThat(repository.findAll()).containsExactly(replacement);
    }

    @Test
    void findsAllOrchestrations() {
        var first = draft(OrchestrationType.FULL_ASSESSMENT);
        var second = draft(OrchestrationType.ASSESSMENT_TO_PUBLICATION);

        repository.save(first);
        repository.save(second);

        assertThat(repository.findAll()).containsExactlyInAnyOrder(first, second);
    }

    @Test
    void findsByType() {
        var full = draft(OrchestrationType.FULL_ASSESSMENT);
        var publication = draft(OrchestrationType.ASSESSMENT_TO_PUBLICATION);

        repository.save(full);
        repository.save(publication);

        assertThat(repository.findByType(OrchestrationType.FULL_ASSESSMENT))
                .containsExactly(full);
    }

    @Test
    void findsByStatus() {
        var draft = draft(OrchestrationType.FULL_ASSESSMENT);
        var running = new Orchestration(
                OrchestrationId.newId(),
                OrchestrationType.ASSESSMENT_TO_KNOWLEDGE,
                OrchestrationStatus.RUNNING,
                new OrchestrationMetadata("Running", "test", Map.of()),
                List.of(OrchestrationStep.pending(1, OrchestrationStepType.ASSESSMENT)));

        repository.save(draft);
        repository.save(running);

        assertThat(repository.findByStatus(OrchestrationStatus.DRAFT)).containsExactly(draft);
        assertThat(repository.findByStatus(OrchestrationStatus.RUNNING)).containsExactly(running);
    }

    @Test
    void checksExistence() {
        var orchestration = draft(OrchestrationType.FULL_ASSESSMENT);

        assertThat(repository.existsById(orchestration.id())).isFalse();
        repository.save(orchestration);
        assertThat(repository.existsById(orchestration.id())).isTrue();
    }

    @Test
    void rejectsNullArguments() {
        assertThatThrownBy(() -> repository.save(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findById(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findByType(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findByStatus(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.existsById(null))
                .isInstanceOf(NullPointerException.class);
    }

    private static Orchestration draft(OrchestrationType type) {
        return Orchestration.draft(
                type,
                new OrchestrationMetadata(type.name(), "test", Map.of()),
                List.of(OrchestrationStep.pending(1, OrchestrationStepType.ASSESSMENT)));
    }
}
