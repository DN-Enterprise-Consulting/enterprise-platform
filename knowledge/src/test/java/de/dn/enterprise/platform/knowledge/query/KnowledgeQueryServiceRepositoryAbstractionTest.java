package de.dn.enterprise.platform.knowledge.query;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectMetadata;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectStatus;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectVersion;
import de.dn.enterprise.platform.knowledge.spi.KnowledgeRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeQueryServiceRepositoryAbstractionTest {

    @Test
    void worksAgainstRepositoryInterfaceWithoutInMemoryDependency() {
        KnowledgeRepository repository = new StubRepository(
                List.of(object(KnowledgeObjectType.RULE, KnowledgeObjectStatus.ACTIVE)));

        var service = new KnowledgeQueryService(repository);

        assertThat(service.find(
                KnowledgeQuery.byTypeAndStatus(
                        KnowledgeObjectType.RULE,
                        KnowledgeObjectStatus.ACTIVE)))
                .hasSize(1);
    }

    private static KnowledgeObject object(
            KnowledgeObjectType type,
            KnowledgeObjectStatus status) {
        return new KnowledgeObject(
                KnowledgeObjectId.newId(),
                type,
                KnowledgeObjectVersion.initial(),
                status,
                new KnowledgeObjectMetadata("Test", "", Map.of()),
                "content");
    }

    private record StubRepository(List<KnowledgeObject> objects)
            implements KnowledgeRepository {

        @Override
        public KnowledgeObject save(KnowledgeObject knowledgeObject) {
            return knowledgeObject;
        }

        @Override
        public Optional<KnowledgeObject> findById(KnowledgeObjectId id) {
            return objects.stream()
                    .filter(object -> object.id().equals(id))
                    .findFirst();
        }

        @Override
        public List<KnowledgeObject> findByType(KnowledgeObjectType type) {
            return objects.stream()
                    .filter(object -> object.type() == type)
                    .toList();
        }

        @Override
        public List<KnowledgeObject> findAll() {
            return objects;
        }

        @Override
        public boolean existsById(KnowledgeObjectId id) {
            return findById(id).isPresent();
        }
    }
}
