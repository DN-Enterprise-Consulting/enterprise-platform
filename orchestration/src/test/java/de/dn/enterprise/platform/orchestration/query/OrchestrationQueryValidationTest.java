package de.dn.enterprise.platform.orchestration.query;

import de.dn.enterprise.platform.orchestration.domain.*;
import de.dn.enterprise.platform.orchestration.inmemory.InMemoryOrchestrationRepository;
import de.dn.enterprise.platform.orchestration.validation.OrchestrationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class OrchestrationQueryValidationTest {
    @Test void rejectsNullQuery() {
        assertThatThrownBy(() -> new OrchestrationQueryService(new InMemoryOrchestrationRepository()).query(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test void validatesObjectsBeforeReturningThem() {
        var repo = new InMemoryOrchestrationRepository();
        var valid = Orchestration.draft(OrchestrationType.FULL_ASSESSMENT,
                new OrchestrationMetadata("x", "d", Map.of()),
                List.of(OrchestrationStep.pending(1, OrchestrationStepType.ASSESSMENT)));
        repo.save(valid);
        assertThat(new OrchestrationQueryService(repo).query(OrchestrationQuery.all())).containsExactly(valid);
    }
}
