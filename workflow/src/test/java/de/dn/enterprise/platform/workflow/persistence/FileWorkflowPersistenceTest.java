package de.dn.enterprise.platform.workflow.persistence;

import de.dn.enterprise.platform.workflow.domain.Workflow;
import de.dn.enterprise.platform.workflow.domain.WorkflowId;
import de.dn.enterprise.platform.workflow.domain.WorkflowMetadata;
import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileWorkflowPersistenceTest {

    @TempDir
    Path tempDirectory;

    @Test
    void savesAndLoadsWorkflowRoundTrip() {
        FileWorkflowPersistence persistence =
                new FileWorkflowPersistence(tempDirectory);
        Workflow workflow = workflow();

        Workflow saved = persistence.save(workflow);
        Workflow loaded = persistence.load(workflow.id()).orElseThrow();

        assertThat(saved).isSameAs(workflow);
        assertThat(loaded).isEqualTo(workflow);
        assertThat(persistence.exists(workflow.id())).isTrue();
    }

    @Test
    void createsStorageDirectoryWhenSaving() throws Exception {
        Path storage = tempDirectory.resolve("nested").resolve("workflows");
        FileWorkflowPersistence persistence =
                new FileWorkflowPersistence(storage);

        persistence.save(workflow());

        assertThat(Files.isDirectory(storage)).isTrue();
        try (var files = Files.list(storage)) {
            assertThat(files.count()).isEqualTo(1);
        }
    }

    @Test
    void returnsEmptyForMissingWorkflow() {
        FileWorkflowPersistence persistence =
                new FileWorkflowPersistence(tempDirectory);

        assertThat(persistence.load(WorkflowId.newId())).isEmpty();
    }

    @Test
    void deletesWorkflow() {
        FileWorkflowPersistence persistence =
                new FileWorkflowPersistence(tempDirectory);
        Workflow workflow = workflow();
        persistence.save(workflow);

        persistence.delete(workflow.id());

        assertThat(persistence.exists(workflow.id())).isFalse();
        assertThat(persistence.load(workflow.id())).isEmpty();
    }

    @Test
    void deleteMissingWorkflowIsNoOp() {
        FileWorkflowPersistence persistence =
                new FileWorkflowPersistence(tempDirectory);

        persistence.delete(WorkflowId.newId());
    }

    @Test
    void preservesMetadataAttributesAndStatus() {
        FileWorkflowPersistence persistence =
                new FileWorkflowPersistence(tempDirectory);
        Workflow workflow = Workflow.draft(
                WorkflowType.RULE_EVALUATION,
                new WorkflowMetadata(
                        "Rule evaluation",
                        "Evaluation workflow",
                        Map.of(
                                "owner", "DN Enterprise Consulting",
                                "scope", "enterprise",
                                "unicode", "ÄÖÜ")))
                .withStatus(WorkflowStatus.RUNNING);

        persistence.save(workflow);

        assertThat(persistence.load(workflow.id())).contains(workflow);
    }

    @Test
    void rejectsNullArguments() {
        FileWorkflowPersistence persistence =
                new FileWorkflowPersistence(tempDirectory);

        assertThatThrownBy(() -> persistence.save(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistence.load(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistence.exists(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistence.delete(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void storesUtf8ValuesBase64Encoded() throws Exception {
        FileWorkflowPersistence persistence =
                new FileWorkflowPersistence(tempDirectory);
        Workflow workflow = Workflow.draft(
                WorkflowType.KNOWLEDGE_PROCESSING,
                new WorkflowMetadata(
                        "ÄÖÜ",
                        "Beschreibung mit UTF-8: äöü",
                        Map.of("key", "Wert")));

        persistence.save(workflow);

        Path file;
        try (var files = Files.list(tempDirectory)) {
            file = files.findFirst().orElseThrow();
        }

        String raw = Files.readString(file, StandardCharsets.UTF_8);
        assertThat(raw).doesNotContain("ÄÖÜ");
        assertThat(raw).doesNotContain("Beschreibung mit UTF-8");
    }

    @Test
    void malformedFileIsReportedAsPersistenceException() throws Exception {
        FileWorkflowPersistence persistence =
                new FileWorkflowPersistence(tempDirectory);
        WorkflowId id = WorkflowId.newId();

        Files.writeString(
                tempDirectory.resolve(id.value() + ".workflow"),
                "broken",
                StandardCharsets.UTF_8);

        assertThatThrownBy(() -> persistence.load(id))
                .isInstanceOf(WorkflowPersistenceException.class);
    }

    private static Workflow workflow() {
        return Workflow.draft(
                WorkflowType.ASSESSMENT,
                new WorkflowMetadata(
                        "Assessment workflow",
                        "Enterprise assessment",
                        Map.of(
                                "owner", "DN Enterprise Consulting",
                                "scope", "enterprise")));
    }
}
