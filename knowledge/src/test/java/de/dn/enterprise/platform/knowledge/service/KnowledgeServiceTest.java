package de.dn.enterprise.platform.knowledge.service;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectMetadata;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectStatus;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.inmemory.InMemoryKnowledgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KnowledgeServiceTest {

    private InMemoryKnowledgeRepository repository;
    private KnowledgeService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryKnowledgeRepository();
        service = new KnowledgeService(repository);
    }

    @Test
    void createsDraftKnowledgeObject() {
        var object = service.create(
                KnowledgeObjectType.STANDARD,
                metadata("Standard"),
                "content");

        assertThat(object.status()).isEqualTo(KnowledgeObjectStatus.DRAFT);
        assertThat(object.type()).isEqualTo(KnowledgeObjectType.STANDARD);
        assertThat(repository.findById(object.id())).contains(object);
    }

    @Test
    void getsExistingObject() {
        var created = service.create(
                KnowledgeObjectType.RULE,
                metadata("Rule"),
                "content");

        assertThat(service.get(created.id())).isEqualTo(created);
    }

    @Test
    void throwsWhenObjectDoesNotExist() {
        assertThatThrownBy(() -> service.get(
                de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId.newId()))
                .isInstanceOf(KnowledgeObjectNotFoundException.class);
    }

    @Test
    void updatesContentAndIncrementsPatchVersion() {
        var created = service.create(
                KnowledgeObjectType.STANDARD,
                metadata("Standard"),
                "old");

        var updated = service.updateContent(
                created.id(),
                metadata("Updated Standard"),
                "new");

        assertThat(updated.id()).isEqualTo(created.id());
        assertThat(updated.version()).isEqualTo(
                created.version().nextPatch());
        assertThat(updated.content()).isEqualTo("new");
        assertThat(updated.metadata().name()).isEqualTo("Updated Standard");
        assertThat(updated.status()).isEqualTo(KnowledgeObjectStatus.DRAFT);
    }

    @Test
    void activatesObject() {
        var created = service.create(
                KnowledgeObjectType.CAPABILITY,
                metadata("Capability"),
                "content");

        var active = service.activate(created.id());

        assertThat(active.status()).isEqualTo(KnowledgeObjectStatus.ACTIVE);
        assertThat(active.version()).isEqualTo(created.version());
    }

    @Test
    void deprecatesObject() {
        var created = service.create(
                KnowledgeObjectType.GUIDELINE,
                metadata("Guideline"),
                "content");

        service.activate(created.id());
        var deprecated = service.deprecate(created.id());

        assertThat(deprecated.status()).isEqualTo(KnowledgeObjectStatus.DEPRECATED);
    }

    private static KnowledgeObjectMetadata metadata(String name) {
        return new KnowledgeObjectMetadata(
                name,
                "M1.7 test object",
                Map.of("source", "M1.7"));
    }
}
