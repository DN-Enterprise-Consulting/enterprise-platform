package de.dn.enterprise.platform.rules.spi;

import de.dn.enterprise.platform.rules.domain.Rule;
import de.dn.enterprise.platform.rules.domain.RuleId;
import de.dn.enterprise.platform.rules.domain.RuleMetadata;
import de.dn.enterprise.platform.rules.domain.RuleStatus;
import de.dn.enterprise.platform.rules.domain.RuleType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RuleRepositoryContractTest {

    @Test
    void contractCanBeImplementedWithoutInfrastructureDependencies() {
        RuleRepository repository = new InMemoryRuleRepositoryStub();
        Rule rule = Rule.draft(RuleType.values()[0],
                new RuleMetadata("Required field", "", Map.of()),
                "field must be present");

        assertThat(repository.save(rule)).isEqualTo(rule);
        assertThat(repository.findById(rule.id())).contains(rule);
        assertThat(repository.findAll()).containsExactly(rule);
        assertThat(repository.findByType(RuleType.values()[0])).containsExactly(rule);
        assertThat(repository.findByStatus(RuleStatus.DRAFT)).containsExactly(rule);
        assertThat(repository.existsById(rule.id())).isTrue();
    }

    private static final class InMemoryRuleRepositoryStub implements RuleRepository {
        private final Map<RuleId, Rule> rules = new HashMap<>();

        @Override
        public Rule save(Rule rule) {
            rules.put(rule.id(), rule);
            return rule;
        }

        @Override
        public Optional<Rule> findById(RuleId id) {
            return Optional.ofNullable(rules.get(id));
        }

        @Override
        public List<Rule> findAll() {
            return List.copyOf(rules.values());
        }

        @Override
        public List<Rule> findByType(RuleType type) {
            return rules.values().stream().filter(rule -> rule.type() == type).toList();
        }

        @Override
        public List<Rule> findByStatus(RuleStatus status) {
            return rules.values().stream().filter(rule -> rule.status() == status).toList();
        }

        @Override
        public boolean existsById(RuleId id) {
            return rules.containsKey(id);
        }
    }
}
