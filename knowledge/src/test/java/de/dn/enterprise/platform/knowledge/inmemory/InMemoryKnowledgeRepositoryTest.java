package de.dn.enterprise.platform.knowledge.inmemory;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectMetadata;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectStatus;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectVersion;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryKnowledgeRepositoryTest {

    @Test
    void findAllReturnsAllObjects() {
        var repository = new InMemoryKnowledgeRepository();
        repository.save(object(KnowledgeObjectType.STANDARD));
        repository.save(object(KnowledgeObjectType.RULE));

        assertThat(repository.findAll()).hasSize(2);
    }

    private static KnowledgeObject object(KnowledgeObjectType type) {
        return new KnowledgeObject(
                de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId.newId(),
                type,
                KnowledgeObjectVersion.initial(),
                KnowledgeObjectStatus.DRAFT,
                new KnowledgeObjectMetadata("Test", "", Map.of()),
                "content");
    }
}
