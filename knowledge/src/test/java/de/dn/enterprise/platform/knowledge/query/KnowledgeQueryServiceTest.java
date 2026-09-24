package de.dn.enterprise.platform.knowledge.query;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectMetadata;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectStatus;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.inmemory.InMemoryKnowledgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeQueryServiceTest {

    private InMemoryKnowledgeRepository repository;
    private KnowledgeQueryService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryKnowledgeRepository();
        service = new KnowledgeQueryService(repository);

        save(KnowledgeObjectType.STANDARD, KnowledgeObjectStatus.DRAFT, "Standard draft");
        save(KnowledgeObjectType.STANDARD, KnowledgeObjectStatus.ACTIVE, "Standard active");
        save(KnowledgeObjectType.RULE, KnowledgeObjectStatus.ACTIVE, "Rule active");
        save(KnowledgeObjectType.RULE, KnowledgeObjectStatus.DEPRECATED, "Rule deprecated");
    }

    @Test
    void findsAllObjects() {
        assertThat(service.find(KnowledgeQuery.all())).hasSize(4);
    }

    @Test
    void filtersByType() {
        assertThat(service.find(KnowledgeQuery.byType(KnowledgeObjectType.RULE)))
                .hasSize(2)
                .allMatch(object -> object.type() == KnowledgeObjectType.RULE);
    }

    @Test
    void filtersByStatus() {
        assertThat(service.find(KnowledgeQuery.byStatus(KnowledgeObjectStatus.ACTIVE)))
                .hasSize(2)
                .allMatch(object -> object.status() == KnowledgeObjectStatus.ACTIVE);
    }

    @Test
    void filtersByTypeAndStatus() {
        var result = service.find(KnowledgeQuery.byTypeAndStatus(
                KnowledgeObjectType.RULE,
                KnowledgeObjectStatus.ACTIVE));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().type()).isEqualTo(KnowledgeObjectType.RULE);
        assertThat(result.getFirst().status()).isEqualTo(KnowledgeObjectStatus.ACTIVE);
    }

    @Test
    void returnsEmptyResultWhenNothingMatches() {
        var result = service.find(KnowledgeQuery.byTypeAndStatus(
                KnowledgeObjectType.CAPABILITY,
                KnowledgeObjectStatus.ARCHIVED));

        assertThat(result).isEmpty();
    }

    private void save(
            KnowledgeObjectType type,
            KnowledgeObjectStatus status,
            String content) {
        repository.save(new KnowledgeObject(
                de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId.newId(),
                type,
                de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectVersion.initial(),
                status,
                new KnowledgeObjectMetadata(content, "", Map.of()),
                content));
    }
}
