package de.dn.enterprise.platform.execution.rules;

import de.dn.enterprise.platform.execution.domain.ExecutionContext;
import de.dn.enterprise.platform.execution.domain.ExecutionResult;
import de.dn.enterprise.platform.rules.domain.RuleStatus;
import de.dn.enterprise.platform.rules.domain.RuleType;
import de.dn.enterprise.platform.rules.inmemory.InMemoryRuleRepository;
import de.dn.enterprise.platform.rules.service.PersistentRuleService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RuleEvaluationAdapterTest {

    @Test
    void executesRuleThroughServiceBoundary() {
        InMemoryRuleRepository repository = new InMemoryRuleRepository();
        PersistentRuleService service = new PersistentRuleService(repository);
        RuleEvaluationAdapter adapter = new RuleEvaluationAdapter(service);

        ExecutionResult result = adapter.execute(ExecutionContext.empty()
                .with(RuleEvaluationAdapter.TYPE_KEY, RuleType.ARCHITECTURE.name())
                .with(RuleEvaluationAdapter.NAME_KEY, "Architecture Rule")
                .with(RuleEvaluationAdapter.DESCRIPTION_KEY, "Execution rule")
                .with(RuleEvaluationAdapter.DEFINITION_KEY, "java.version >= 21"));

        assertThat(result.successful()).isTrue();
        assertThat(result.outputs()).containsKeys(
                "ruleId", "ruleType", "ruleStatus", "ruleDefinition");
        assertThat(result.outputs()).containsEntry(
                "ruleType", RuleType.ARCHITECTURE.name());
        assertThat(result.outputs()).containsEntry(
                "ruleStatus", RuleStatus.ACTIVE.name());
        assertThat(result.outputs()).containsEntry(
                "ruleDefinition", "java.version >= 21");
        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findAll().get(0).status()).isEqualTo(RuleStatus.ACTIVE);
    }

    @Test
    void rejectsMissingRuleDefinition() {
        RuleEvaluationAdapter adapter = adapter();

        assertThatThrownBy(() -> adapter.execute(ExecutionContext.empty()
                .with(RuleEvaluationAdapter.TYPE_KEY, RuleType.ARCHITECTURE.name())
                .with(RuleEvaluationAdapter.NAME_KEY, "Architecture Rule")
                .with(RuleEvaluationAdapter.DESCRIPTION_KEY, "Description")))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void exposesStableDomainKey() {
        assertThat(adapter().domainKey()).isEqualTo("rules");
    }

    private RuleEvaluationAdapter adapter() {
        return new RuleEvaluationAdapter(
                new PersistentRuleService(new InMemoryRuleRepository()));
    }
}
