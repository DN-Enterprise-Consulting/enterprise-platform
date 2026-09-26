package de.dn.enterprise.platform.execution.knowledge;

import de.dn.enterprise.platform.execution.domain.ExecutionContext;
import de.dn.enterprise.platform.execution.domain.ExecutionResult;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectStatus;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.inmemory.InMemoryKnowledgeRepository;
import de.dn.enterprise.platform.knowledge.service.PersistentKnowledgeService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KnowledgeExecutionAdapterTest {

    @Test
    void executesKnowledgeCreationAndActivationThroughServiceBoundary() {
        InMemoryKnowledgeRepository repository = new InMemoryKnowledgeRepository();
        PersistentKnowledgeService service = new PersistentKnowledgeService(repository);
        KnowledgeExecutionAdapter adapter = new KnowledgeExecutionAdapter(service);

        ExecutionResult result = adapter.execute(ExecutionContext.empty()
                .with(KnowledgeExecutionAdapter.TYPE_KEY, KnowledgeObjectType.STANDARD.name())
                .with(KnowledgeExecutionAdapter.NAME_KEY, "Execution Knowledge")
                .with(KnowledgeExecutionAdapter.DESCRIPTION_KEY, "Created by execution adapter")
                .with(KnowledgeExecutionAdapter.CONTENT_KEY, "Knowledge content"));

        assertThat(result.successful()).isTrue();
        assertThat(result.outputs()).containsKeys(
                "knowledgeId", "knowledgeType", "knowledgeStatus", "knowledgeVersion");
        assertThat(result.outputs()).containsEntry(
                "knowledgeType", KnowledgeObjectType.STANDARD.name());
        assertThat(result.outputs()).containsEntry(
                "knowledgeStatus", KnowledgeObjectStatus.ACTIVE.name());
        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findAll().get(0).status()).isEqualTo(KnowledgeObjectStatus.ACTIVE);
    }

    @Test
    void rejectsMissingKnowledgeContent() {
        KnowledgeExecutionAdapter adapter = adapter();

        assertThatThrownBy(() -> adapter.execute(ExecutionContext.empty()
                .with(KnowledgeExecutionAdapter.TYPE_KEY, KnowledgeObjectType.STANDARD.name())
                .with(KnowledgeExecutionAdapter.NAME_KEY, "Knowledge")
                .with(KnowledgeExecutionAdapter.DESCRIPTION_KEY, "Description")))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void exposesStableDomainKey() {
        assertThat(adapter().domainKey()).isEqualTo("knowledge");
    }

    private KnowledgeExecutionAdapter adapter() {
        return new KnowledgeExecutionAdapter(
                new PersistentKnowledgeService(new InMemoryKnowledgeRepository()));
    }
}
