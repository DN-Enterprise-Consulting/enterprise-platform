package de.dn.enterprise.platform.workflow.query;

import de.dn.enterprise.platform.workflow.domain.WorkflowStatus;
import de.dn.enterprise.platform.workflow.domain.WorkflowType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WorkflowQueryTest {

    @Test
    void allCreatesUnrestrictedQuery() {
        WorkflowQuery query = WorkflowQuery.all();
        assertThat(query.type()).isEmpty();
        assertThat(query.status()).isEmpty();
    }

    @Test
    void byTypeCreatesTypeFilter() {
        WorkflowQuery query = WorkflowQuery.byType(WorkflowType.ASSESSMENT);
        assertThat(query.type()).contains(WorkflowType.ASSESSMENT);
        assertThat(query.status()).isEmpty();
    }

    @Test
    void byStatusCreatesStatusFilter() {
        WorkflowQuery query = WorkflowQuery.byStatus(WorkflowStatus.RUNNING);
        assertThat(query.type()).isEmpty();
        assertThat(query.status()).contains(WorkflowStatus.RUNNING);
    }

    @Test
    void byTypeAndStatusCreatesBothFilters() {
        WorkflowQuery query = WorkflowQuery.byTypeAndStatus(
                WorkflowType.PUBLICATION, WorkflowStatus.COMPLETED);
        assertThat(query.type()).contains(WorkflowType.PUBLICATION);
        assertThat(query.status()).contains(WorkflowStatus.COMPLETED);
    }
}
