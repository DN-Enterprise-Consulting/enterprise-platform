package de.dn.enterprise.platform.orchestration.persistence;

import de.dn.enterprise.platform.orchestration.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileOrchestrationPersistenceTest {

    @TempDir
    Path tempDir;

    @Test
    void savesLoadsAndPreservesAggregate() {
        var persistence = new FileOrchestrationPersistence(tempDir.resolve("store"));
        var original = sample();

        persistence.save(original);

        assertThat(persistence.exists(original.id())).isTrue();
        assertThat(persistence.load(original.id())).isEqualTo(original);
    }

    @Test
    void createsStorageDirectoryOnSave() {
        var directory = tempDir.resolve("nested/store");
        var persistence = new FileOrchestrationPersistence(directory);

        persistence.save(sample());

        assertThat(Files.isDirectory(directory)).isTrue();
    }

    @Test
    void returnsNullForMissingFile() {
        var persistence = new FileOrchestrationPersistence(tempDir);
        assertThat(persistence.load(OrchestrationId.newId())).isNull();
    }

    @Test
    void deletesPersistedAggregate() {
        var persistence = new FileOrchestrationPersistence(tempDir);
        var orchestration = sample();

        persistence.save(orchestration);
        persistence.delete(orchestration.id());

        assertThat(persistence.exists(orchestration.id())).isFalse();
        assertThat(persistence.load(orchestration.id())).isNull();
    }

    @Test
    void handlesMissingDeleteAsNoOp() {
        var persistence = new FileOrchestrationPersistence(tempDir);
        persistence.delete(OrchestrationId.newId());
    }

    @Test
    void rejectsNullArguments() {
        var persistence = new FileOrchestrationPersistence(tempDir);

        assertThatThrownBy(() -> persistence.save(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistence.load(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistence.exists(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistence.delete(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsCorruptFile() throws Exception {
        var persistence = new FileOrchestrationPersistence(tempDir);
        var id = OrchestrationId.newId();

        Files.writeString(tempDir.resolve(id.value() + ".orchestration"), "corrupt");

        assertThatThrownBy(() -> persistence.load(id))
                .isInstanceOf(OrchestrationPersistenceException.class);
    }

    @Test
    void serializesAttributesDeterministically() throws Exception {
        var persistence = new FileOrchestrationPersistence(tempDir);
        var orchestration = Orchestration.draft(
                OrchestrationType.FULL_ASSESSMENT,
                new OrchestrationMetadata(
                        "Deterministic",
                        "test",
                        Map.of("z", "last", "a", "first")),
                List.of(OrchestrationStep.pending(1, OrchestrationStepType.ASSESSMENT)));

        persistence.save(orchestration);

        var lines = Files.readAllLines(
                tempDir.resolve(orchestration.id().value() + ".orchestration"));

        assertThat(lines).hasSize(7);
        assertThat(lines.get(5)).isNotBlank();
    }

    private static Orchestration sample() {
        return new Orchestration(
                OrchestrationId.newId(),
                OrchestrationType.FULL_ASSESSMENT,
                OrchestrationStatus.RUNNING,
                new OrchestrationMetadata(
                        "Full Assessment",
                        "Persistence test",
                        Map.of("owner", "platform", "environment", "test")),
                List.of(
                        new OrchestrationStep(1, OrchestrationStepType.ASSESSMENT, OrchestrationStepStatus.COMPLETED),
                        new OrchestrationStep(2, OrchestrationStepType.RULE_EVALUATION, OrchestrationStepStatus.RUNNING),
                        new OrchestrationStep(3, OrchestrationStepType.KNOWLEDGE_PROCESSING, OrchestrationStepStatus.PENDING),
                        new OrchestrationStep(4, OrchestrationStepType.PUBLICATION, OrchestrationStepStatus.PENDING)));
    }
}
