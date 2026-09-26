package de.dn.enterprise.platform.execution.domain;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExecutionContextResultModelTest {

    @Test
    void contextIsImmutable() {
        ExecutionContext original = ExecutionContext.empty();
        ExecutionContext updated = original.with("assessmentId", "a-1");

        assertThat(original.values()).isEmpty();
        assertThat(updated.get("assessmentId")).contains("a-1");
    }

    @Test
    void contextCanRemoveValue() {
        ExecutionContext context = ExecutionContext.empty()
                .with("assessmentId", "a-1")
                .with("environment", "test");

        ExecutionContext updated = context.without("environment");

        assertThat(updated.get("assessmentId")).contains("a-1");
        assertThat(updated.get("environment")).isEmpty();
        assertThat(context.get("environment")).contains("test");
    }

    @Test
    void contextRejectsNullKeyAndValue() {
        assertThatThrownBy(() -> ExecutionContext.empty().with(null, "x"))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> ExecutionContext.empty().with("x", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void successfulResultCanContainOutputs() {
        ExecutionResult result = ExecutionResult.success(
                "assessment completed",
                Map.of("assessmentId", "a-1"));

        assertThat(result.status()).isEqualTo(ExecutionResultStatus.SUCCESS);
        assertThat(result.successful()).isTrue();
        assertThat(result.outputs()).containsEntry("assessmentId", "a-1");
    }

    @Test
    void failureResultIsNotSuccessful() {
        ExecutionResult result = ExecutionResult.failure("step failed");

        assertThat(result.status()).isEqualTo(ExecutionResultStatus.FAILURE);
        assertThat(result.successful()).isFalse();
    }

    @Test
    void resultRequiresSummary() {
        assertThatThrownBy(() -> ExecutionResult.success(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void resultStoreIsImmutable() {
        ExecutionResult first = ExecutionResult.success("first");
        ExecutionResult second = ExecutionResult.success("second");

        ExecutionResultStore original = ExecutionResultStore.empty()
                .with("assessment", first);
        ExecutionResultStore updated = original.with("rules", second);

        assertThat(original.get("rules")).isEmpty();
        assertThat(updated.get("assessment")).contains(first);
        assertThat(updated.get("rules")).contains(second);
    }
}
