package de.dn.enterprise.platform.orchestration.query;

import de.dn.enterprise.platform.orchestration.domain.Orchestration;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationMetadata;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStatus;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStep;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStepType;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationType;
import de.dn.enterprise.platform.orchestration.inmemory.InMemoryOrchestrationRepository;
import de.dn.enterprise.platform.orchestration.spi.OrchestrationRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrchestrationQueryServiceTest {

    private static OrchestrationMetadata metadata(String name) {
        return new OrchestrationMetadata(name, name + " description", Map.of("owner", "test"));
    }

    private static List<OrchestrationStep> steps() {
        return List.of(
                OrchestrationStep.pending(1, OrchestrationStepType.ASSESSMENT),
                OrchestrationStep.pending(2, OrchestrationStepType.RULE_EVALUATION));
    }

    private static Orchestration orchestration(OrchestrationType type, OrchestrationStatus status, String name) {
        Orchestration orchestration = Orchestration.draft(type, metadata(name), steps());
        if (status == OrchestrationStatus.RUNNING) {
            return new Orchestration(orchestration.id(), orchestration.type(), OrchestrationStatus.RUNNING,
                    orchestration.metadata(), orchestration.steps());
        }
        if (status == OrchestrationStatus.COMPLETED) {
            return new Orchestration(orchestration.id(), orchestration.type(), OrchestrationStatus.COMPLETED,
                    orchestration.metadata(), orchestration.steps());
        }
        if (status == OrchestrationStatus.FAILED) {
            return new Orchestration(orchestration.id(), orchestration.type(), OrchestrationStatus.FAILED,
                    orchestration.metadata(), orchestration.steps());
        }
        return orchestration;
    }

    private static OrchestrationQueryService serviceWithSampleData() {
        OrchestrationRepository repository = new InMemoryOrchestrationRepository();
        repository.save(orchestration(OrchestrationType.FULL_ASSESSMENT, OrchestrationStatus.DRAFT, "full-draft"));
        repository.save(orchestration(OrchestrationType.FULL_ASSESSMENT, OrchestrationStatus.COMPLETED, "full-completed"));
        repository.save(orchestration(OrchestrationType.ASSESSMENT_TO_PUBLICATION, OrchestrationStatus.RUNNING, "publication-running"));
        repository.save(orchestration(OrchestrationType.ASSESSMENT_TO_PUBLICATION, OrchestrationStatus.FAILED, "publication-failed"));
        return new OrchestrationQueryService(repository);
    }

    @Test
    void allReturnsAllOrchestrations() {
        assertThat(serviceWithSampleData().query(OrchestrationQuery.all())).hasSize(4);
    }

    @Test
    void filtersByType() {
        List<Orchestration> result = serviceWithSampleData()
                .query(OrchestrationQuery.byType(OrchestrationType.ASSESSMENT_TO_PUBLICATION));
        assertThat(result).hasSize(2)
                .allMatch(orchestration -> orchestration.type() == OrchestrationType.ASSESSMENT_TO_PUBLICATION);
    }

    @Test
    void filtersByStatus() {
        List<Orchestration> result = serviceWithSampleData()
                .query(OrchestrationQuery.byStatus(OrchestrationStatus.COMPLETED));
        assertThat(result).hasSize(1)
                .allMatch(orchestration -> orchestration.status() == OrchestrationStatus.COMPLETED);
    }

    @Test
    void combinesTypeAndStatusWithAndSemantics() {
        List<Orchestration> result = serviceWithSampleData().query(
                OrchestrationQuery.byTypeAndStatus(
                        OrchestrationType.ASSESSMENT_TO_PUBLICATION, OrchestrationStatus.FAILED));
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().type()).isEqualTo(OrchestrationType.ASSESSMENT_TO_PUBLICATION);
        assertThat(result.getFirst().status()).isEqualTo(OrchestrationStatus.FAILED);
    }

    @Test
    void returnsEmptyWhenNothingMatches() {
        assertThat(serviceWithSampleData().query(
                OrchestrationQuery.byTypeAndStatus(
                        OrchestrationType.ASSESSMENT_TO_KNOWLEDGE, OrchestrationStatus.COMPLETED)))
                .isEmpty();
    }

    @Test
    void rejectsNullQuery() {
        assertThatThrownBy(() -> serviceWithSampleData().query(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("query must not be null");
    }
}
