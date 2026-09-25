package de.dn.enterprise.platform.rules.inmemory;

import de.dn.enterprise.platform.rules.domain.Rule;
import de.dn.enterprise.platform.rules.domain.RuleId;
import de.dn.enterprise.platform.rules.domain.RuleMetadata;
import de.dn.enterprise.platform.rules.domain.RuleStatus;
import de.dn.enterprise.platform.rules.domain.RuleType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryRuleRepositoryTest {

    @Test
    void savesAndFindsById() {
        InMemoryRuleRepository repository = new InMemoryRuleRepository();
        Rule rule = rule(RuleType.ARCHITECTURE, RuleStatus.DRAFT, "architecture rule");

        assertThat(repository.save(rule)).isEqualTo(rule);
        assertThat(repository.findById(rule.id())).contains(rule);
        assertThat(repository.existsById(rule.id())).isTrue();
    }

    @Test
    void returnsEmptyForUnknownId() {
        InMemoryRuleRepository repository = new InMemoryRuleRepository();

        assertThat(repository.findById(new RuleId(UUID.randomUUID()))).isEmpty();
        assertThat(repository.existsById(new RuleId(UUID.randomUUID()))).isFalse();
    }

    @Test
    void findsByType() {
        InMemoryRuleRepository repository = new InMemoryRuleRepository();
        Rule architecture = rule(RuleType.ARCHITECTURE, RuleStatus.DRAFT, "architecture");
        Rule security = rule(RuleType.SECURITY, RuleStatus.DRAFT, "security");
        repository.save(architecture);
        repository.save(security);

        assertThat(repository.findByType(RuleType.ARCHITECTURE)).containsExactly(architecture);
    }

    @Test
    void findsByStatus() {
        InMemoryRuleRepository repository = new InMemoryRuleRepository();
        Rule draft = rule(RuleType.ARCHITECTURE, RuleStatus.DRAFT, "draft");
        Rule active = rule(RuleType.SECURITY, RuleStatus.ACTIVE, "active");
        repository.save(draft);
        repository.save(active);

        assertThat(repository.findByStatus(RuleStatus.ACTIVE)).containsExactly(active);
    }

    @Test
    void replacementBySameIdKeepsSingleEntry() {
        InMemoryRuleRepository repository = new InMemoryRuleRepository();
        Rule original = rule(RuleType.ARCHITECTURE, RuleStatus.DRAFT, "original");
        Rule replacement = new Rule(original.id(), RuleType.ARCHITECTURE, RuleStatus.ACTIVE,
                original.metadata(), "replacement");

        repository.save(original);
        repository.save(replacement);

        assertThat(repository.size()).isEqualTo(1);
        assertThat(repository.findById(original.id())).contains(replacement);
    }

    @Test
    void rejectsNullArguments() {
        InMemoryRuleRepository repository = new InMemoryRuleRepository();

        assertThatThrownBy(() -> repository.save(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findById(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findByType(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findByStatus(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.existsById(null)).isInstanceOf(NullPointerException.class);
    }

    private static Rule rule(RuleType type, RuleStatus status, String definition) {
        return new Rule(RuleId.newId(), type, status,
                new RuleMetadata(definition, "", java.util.Map.of()), definition);
    }
}
