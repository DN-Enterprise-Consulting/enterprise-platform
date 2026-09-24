package de.dn.enterprise.platform.knowledge.spi;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectMetadata;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgePersistenceContractTest {

    @Test
    void persistenceContractSupportsSaveLoadExistenceAndDelete() {
        KnowledgePersistence persistence = new StubPersistence();

        KnowledgeObject object = KnowledgeObject.draft(
                KnowledgeObjectType.STANDARD,
                new KnowledgeObjectMetadata(
                        "Persistence Contract Test",
                        "Test object for persistence contract",
                        Map.of()),
                "Persistence contract test content");

        KnowledgeObject saved = persistence.save(object);

        assertThat(saved).isEqualTo(object);
        assertThat(persistence.exists(object.id())).isTrue();
        assertThat(persistence.load(object.id())).contains(object);

        persistence.delete(object.id());

        assertThat(persistence.exists(object.id())).isFalse();
        assertThat(persistence.load(object.id())).isEmpty();
    }

    private static final class StubPersistence implements KnowledgePersistence {
        private final java.util.Map<KnowledgeObjectId, KnowledgeObject> store =
                new java.util.HashMap<>();

        @Override
        public KnowledgeObject save(KnowledgeObject knowledgeObject) {
            store.put(knowledgeObject.id(), knowledgeObject);
            return knowledgeObject;
        }

        @Override
        public Optional<KnowledgeObject> load(KnowledgeObjectId id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public boolean exists(KnowledgeObjectId id) {
            return store.containsKey(id);
        }

        @Override
        public void delete(KnowledgeObjectId id) {
            store.remove(id);
        }
    }
}
