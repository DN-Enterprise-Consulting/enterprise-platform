package de.dn.enterprise.platform.orchestration.query;

import de.dn.enterprise.platform.orchestration.domain.OrchestrationStatus;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrchestrationQueryTest {

    @Test
    void allCreatesUnrestrictedQuery() {
        OrchestrationQuery query = OrchestrationQuery.all();
        assertThat(query.type()).isEmpty();
        assertThat(query.status()).isEmpty();
    }

    @Test
    void byTypeCreatesTypeFilter() {
        OrchestrationQuery query = OrchestrationQuery.byType(OrchestrationType.FULL_ASSESSMENT);
        assertThat(query.type()).contains(OrchestrationType.FULL_ASSESSMENT);
        assertThat(query.status()).isEmpty();
    }

    @Test
    void byStatusCreatesStatusFilter() {
        OrchestrationQuery query = OrchestrationQuery.byStatus(OrchestrationStatus.RUNNING);
        assertThat(query.type()).isEmpty();
        assertThat(query.status()).contains(OrchestrationStatus.RUNNING);
    }

    @Test
    void byTypeAndStatusCreatesBothFilters() {
        OrchestrationQuery query = OrchestrationQuery.byTypeAndStatus(
                OrchestrationType.ASSESSMENT_TO_PUBLICATION, OrchestrationStatus.COMPLETED);
        assertThat(query.type()).contains(OrchestrationType.ASSESSMENT_TO_PUBLICATION);
        assertThat(query.status()).contains(OrchestrationStatus.COMPLETED);
    }
}
