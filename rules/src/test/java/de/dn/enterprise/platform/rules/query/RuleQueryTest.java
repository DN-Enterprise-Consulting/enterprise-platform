package de.dn.enterprise.platform.rules.query;

import de.dn.enterprise.platform.rules.domain.RuleStatus;
import de.dn.enterprise.platform.rules.domain.RuleType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RuleQueryTest {

    @Test
    void createsAllQuery() {
        RuleQuery query = RuleQuery.all();

        assertThat(query.type()).isEmpty();
        assertThat(query.status()).isEmpty();
    }

    @Test
    void createsTypeQuery() {
        RuleType type = RuleType.values()[0];

        RuleQuery query = RuleQuery.byType(type);

        assertThat(query.type()).contains(type);
        assertThat(query.status()).isEmpty();
    }

    @Test
    void createsStatusQuery() {
        RuleQuery query = RuleQuery.byStatus(RuleStatus.DRAFT);

        assertThat(query.type()).isEmpty();
        assertThat(query.status()).contains(RuleStatus.DRAFT);
    }

    @Test
    void createsCombinedQuery() {
        RuleType type = RuleType.values()[0];

        RuleQuery query = RuleQuery.byTypeAndStatus(type, RuleStatus.DRAFT);

        assertThat(query.type()).contains(type);
        assertThat(query.status()).contains(RuleStatus.DRAFT);
    }
}
