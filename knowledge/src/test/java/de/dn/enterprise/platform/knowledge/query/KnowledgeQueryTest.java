package de.dn.enterprise.platform.knowledge.query;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectStatus;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeQueryTest {

    @Test
    void createsAllQuery() {
        var query = KnowledgeQuery.all();

        assertThat(query.type()).isEmpty();
        assertThat(query.status()).isEmpty();
    }

    @Test
    void createsTypeQuery() {
        var query = KnowledgeQuery.byType(KnowledgeObjectType.STANDARD);

        assertThat(query.type()).contains(KnowledgeObjectType.STANDARD);
        assertThat(query.status()).isEmpty();
    }

    @Test
    void createsStatusQuery() {
        var query = KnowledgeQuery.byStatus(KnowledgeObjectStatus.ACTIVE);

        assertThat(query.type()).isEmpty();
        assertThat(query.status()).contains(KnowledgeObjectStatus.ACTIVE);
    }

    @Test
    void createsCombinedQuery() {
        var query = KnowledgeQuery.byTypeAndStatus(
                KnowledgeObjectType.RULE,
                KnowledgeObjectStatus.ACTIVE);

        assertThat(query.type()).contains(KnowledgeObjectType.RULE);
        assertThat(query.status()).contains(KnowledgeObjectStatus.ACTIVE);
    }
}
