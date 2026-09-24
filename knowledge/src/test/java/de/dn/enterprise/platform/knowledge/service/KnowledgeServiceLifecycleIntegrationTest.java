package de.dn.enterprise.platform.knowledge.service;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectMetadata;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectStatus;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.inmemory.InMemoryKnowledgeRepository;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KnowledgeServiceLifecycleIntegrationTest {

    @Test
    void followsCompleteLifecycle() {
        var service = new KnowledgeService(new InMemoryKnowledgeRepository());

        var draft = service.create(
                KnowledgeObjectType.STANDARD,
                metadata("Standard"),
                "content");

        var active = service.activate(draft.id());
        var deprecated = service.deprecate(active.id());
        var archived = service.archive(deprecated.id());

        assertThat(draft.status()).isEqualTo(KnowledgeObjectStatus.DRAFT);
        assertThat(active.status()).isEqualTo(KnowledgeObjectStatus.ACTIVE);
        assertThat(deprecated.status()).isEqualTo(KnowledgeObjectStatus.DEPRECATED);
        assertThat(archived.status()).isEqualTo(KnowledgeObjectStatus.ARCHIVED);
    }

    @Test
    void doesNotAllowUpdatesToArchivedObjects() {
        var service = new KnowledgeService(new InMemoryKnowledgeRepository());

        var object = service.create(
                KnowledgeObjectType.STANDARD,
                metadata("Standard"),
                "content");

        service.archive(service.deprecate(service.activate(object.id()).id()).id());

        assertThatThrownBy(() -> service.updateContent(
                object.id(), metadata("Changed"), "new content"))
                .isInstanceOf(KnowledgeLifecycleException.class);
    }

    private static KnowledgeObjectMetadata metadata(String name) {
        return new KnowledgeObjectMetadata(name, "", Map.of());
    }
}
