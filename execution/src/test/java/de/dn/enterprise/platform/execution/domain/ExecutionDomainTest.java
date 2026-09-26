package de.dn.enterprise.platform.execution.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExecutionDomainTest {

    @Test
    void createsDraftExecution() {
        Execution execution = Execution.draft(
                ExecutionType.ORCHESTRATION,
                ExecutionMetadata.of("assessment execution", "test"));

        assertThat(execution.id()).isNotNull();
        assertThat(execution.type()).isEqualTo(ExecutionType.ORCHESTRATION);
        assertThat(execution.status()).isEqualTo(ExecutionStatus.DRAFT);
    }

    @Test
    void lifecycleIsImmutable() {
        Execution draft = Execution.draft(
                ExecutionType.ORCHESTRATION,
                ExecutionMetadata.of("execution", "test"));

        Execution running = draft.start();
        Execution completed = running.complete();

        assertThat(draft.status()).isEqualTo(ExecutionStatus.DRAFT);
        assertThat(running.status()).isEqualTo(ExecutionStatus.RUNNING);
        assertThat(completed.status()).isEqualTo(ExecutionStatus.COMPLETED);
    }

    @Test
    void failureIsOnlyAllowedFromRunning() {
        Execution draft = Execution.draft(
                ExecutionType.ORCHESTRATION,
                ExecutionMetadata.of("execution", "test"));

        assertThatThrownBy(draft::fail)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void terminalStatesCannotBeChanged() {
        Execution completed = Execution.draft(
                ExecutionType.ORCHESTRATION,
                ExecutionMetadata.of("execution", "test"))
                .start()
                .complete();

        assertThatThrownBy(completed::start)
                .isInstanceOf(IllegalStateException.class);

        assertThatThrownBy(completed::fail)
                .isInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> completed.updateMetadata(
                ExecutionMetadata.of("changed", "not allowed")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void metadataCanOnlyBeChangedInDraft() {
        Execution draft = Execution.draft(
                ExecutionType.ORCHESTRATION,
                ExecutionMetadata.of("execution", "test"));

        Execution updated = draft.updateMetadata(
                ExecutionMetadata.of("updated", "changed"));

        assertThat(updated.metadata().name()).isEqualTo("updated");
        assertThat(draft.metadata().name()).isEqualTo("execution");
    }

    @Test
    void nullsAreRejected() {
        assertThatThrownBy(() -> new Execution(null,
                ExecutionType.ORCHESTRATION,
                ExecutionStatus.DRAFT,
                ExecutionMetadata.of("execution", "test")))
                .isInstanceOf(NullPointerException.class);
    }
}
