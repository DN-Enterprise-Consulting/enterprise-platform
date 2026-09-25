package de.dn.enterprise.platform.orchestration.persistence;

import de.dn.enterprise.platform.orchestration.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileOrchestrationRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void savesAndFindsById() {
        var repository = repository();
        var orchestration = sample(OrchestrationType.FULL_ASSESSMENT, OrchestrationStatus.DRAFT);

        assertThat(repository.save(orchestration)).isSameAs(orchestration);
        assertThat(repository.findById(orchestration.id())).contains(orchestration);
        assertThat(repository.existsById(orchestration.id())).isTrue();
    }

    @Test
    void returnsEmptyForUnknownId() {
        assertThat(repository().findById(OrchestrationId.newId())).isEmpty();
    }

    @Test
    void findsAllPersistedOrchestrations() {
        var repository = repository();
        var first = sample(OrchestrationType.FULL_ASSESSMENT, OrchestrationStatus.DRAFT);
        var second = sample(OrchestrationType.ASSESSMENT_TO_KNOWLEDGE, OrchestrationStatus.RUNNING);

        repository.save(first);
        repository.save(second);

        assertThat(repository.findAll()).containsExactlyInAnyOrder(first, second);
    }

    @Test
    void findsByType() {
        var repository = repository();
        var matching = sample(OrchestrationType.FULL_ASSESSMENT, OrchestrationStatus.DRAFT);
        var other = sample(OrchestrationType.ASSESSMENT_TO_PUBLICATION, OrchestrationStatus.DRAFT);

        repository.save(matching);
        repository.save(other);

        assertThat(repository.findByType(OrchestrationType.FULL_ASSESSMENT))
                .containsExactly(matching);
    }

    @Test
    void findsByStatus() {
        var repository = repository();
        var matching = sample(OrchestrationType.FULL_ASSESSMENT, OrchestrationStatus.RUNNING);
        var other = sample(OrchestrationType.ASSESSMENT_TO_PUBLICATION, OrchestrationStatus.DRAFT);

        repository.save(matching);
        repository.save(other);

        assertThat(repository.findByStatus(OrchestrationStatus.RUNNING))
                .containsExactly(matching);
    }

    @Test
    void persistsAcrossRepositoryRecreation() {
        var storage = tempDir.resolve("store");
        var firstRepository = new FileOrchestrationRepository(new FileOrchestrationPersistence(storage));
        var orchestration = sample(OrchestrationType.ASSESSMENT_TO_PUBLICATION, OrchestrationStatus.COMPLETED);

        firstRepository.save(orchestration);

        var restartedRepository = new FileOrchestrationRepository(new FileOrchestrationPersistence(storage));
        assertThat(restartedRepository.findById(orchestration.id())).contains(orchestration);
        assertThat(restartedRepository.findAll()).containsExactly(orchestration);
    }

    @Test
    void rejectsNullArguments() {
        var repository = repository();

        assertThatThrownBy(() -> repository.save(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findById(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findByType(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findByStatus(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.existsById(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void replacesExistingAggregateWithSameId() {
        var repository = repository();
        var id = OrchestrationId.newId();
        var first = new Orchestration(id, OrchestrationType.FULL_ASSESSMENT, OrchestrationStatus.DRAFT,
                metadata("first"), steps());
        var replacement = new Orchestration(id, OrchestrationType.FULL_ASSESSMENT, OrchestrationStatus.RUNNING,
                metadata("replacement"), steps());

        repository.save(first);
        repository.save(replacement);

        assertThat(repository.findById(id)).contains(replacement);
        assertThat(repository.findAll()).containsExactly(replacement);
    }

    private FileOrchestrationRepository repository() {
        return new FileOrchestrationRepository(
                new FileOrchestrationPersistence(tempDir.resolve("store")));
    }

    private static Orchestration sample(OrchestrationType type, OrchestrationStatus status) {
        return new Orchestration(
                OrchestrationId.newId(), type, status,
                metadata(type.name()), steps());
    }

    private static OrchestrationMetadata metadata(String name) {
        return new OrchestrationMetadata(name, "repository test", Map.of("source", "m66"));
    }

    private static List<OrchestrationStep> steps() {
        return List.of(OrchestrationStep.pending(1, OrchestrationStepType.ASSESSMENT));
    }
}
