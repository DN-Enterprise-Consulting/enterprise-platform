package de.dn.enterprise.platform.knowledge.service;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectMetadata;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.inmemory.InMemoryKnowledgeRepository;
import de.dn.enterprise.platform.knowledge.validation.KnowledgeValidationException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KnowledgeServiceValidationTest {

    @Test
    void rejectsInvalidObjectBeforeSavingOnCreate() {
        var repository = new InMemoryKnowledgeRepository();
        var service = new KnowledgeService(repository);

        assertThatThrownBy(() -> service.create(
                KnowledgeObjectType.STANDARD,
                new KnowledgeObjectMetadata("Valid", "", Map.of()),
                " "))
                .isInstanceOf(KnowledgeValidationException.class);

        assert repository.size() == 0;
    }

    @Test
    void rejectsInvalidObjectBeforeSavingOnUpdate() {
        var repository = new InMemoryKnowledgeRepository();
        var service = new KnowledgeService(repository);

        var created = service.create(
                KnowledgeObjectType.STANDARD,
                new KnowledgeObjectMetadata("Valid", "", Map.of()),
                "content");

        assertThatThrownBy(() -> service.updateContent(
                created.id(),
                new KnowledgeObjectMetadata("Updated", "", Map.of()),
                " "))
                .isInstanceOf(KnowledgeValidationException.class);

        assert repository.findById(created.id()).orElseThrow().equals(created);
    }
}
