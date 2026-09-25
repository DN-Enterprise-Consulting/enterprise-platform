package de.dn.enterprise.platform.rules.query;

import de.dn.enterprise.platform.rules.domain.Rule;
import de.dn.enterprise.platform.rules.domain.RuleId;
import de.dn.enterprise.platform.rules.domain.RuleMetadata;
import de.dn.enterprise.platform.rules.domain.RuleStatus;
import de.dn.enterprise.platform.rules.domain.RuleType;
import de.dn.enterprise.platform.rules.spi.RuleRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RuleQueryServiceTest {

    @Test
    void returnsAllRules() {
        Rule first = rule(RuleStatus.DRAFT);
        Rule second = rule(RuleStatus.ACTIVE);

        RuleQueryService service = new RuleQueryService(repository(first, second));

        assertThat(service.query(RuleQuery.all()))
                .containsExactly(first, second);
    }

    @Test
    void filtersByType() {
        RuleType type = RuleType.values()[0];
        Rule matching = rule(type, RuleStatus.DRAFT);
        Rule other = rule(otherType(type), RuleStatus.DRAFT);

        RuleQueryService service = new RuleQueryService(repository(matching, other));

        assertThat(service.query(RuleQuery.byType(type)))
                .containsExactly(matching);
    }

    @Test
    void filtersByStatus() {
        Rule draft = rule(RuleStatus.DRAFT);
        Rule active = rule(RuleStatus.ACTIVE);

        RuleQueryService service = new RuleQueryService(repository(draft, active));

        assertThat(service.query(RuleQuery.byStatus(RuleStatus.ACTIVE)))
                .containsExactly(active);
    }

    @Test
    void combinesTypeAndStatusWithAndSemantics() {
        RuleType type = RuleType.values()[0];
        Rule matching = rule(type, RuleStatus.ACTIVE);
        Rule sameTypeWrongStatus = rule(type, RuleStatus.DRAFT);
        Rule wrongTypeSameStatus = rule(otherType(type), RuleStatus.ACTIVE);

        RuleQueryService service = new RuleQueryService(
                repository(matching, sameTypeWrongStatus, wrongTypeSameStatus));

        assertThat(service.query(RuleQuery.byTypeAndStatus(type, RuleStatus.ACTIVE)))
                .containsExactly(matching);
    }

    @Test
    void dependsOnlyOnRuleRepository() {
        Rule rule = rule(RuleStatus.DRAFT);
        RuleQueryService service = new RuleQueryService(repository(rule));

        assertThat(service.query(RuleQuery.all())).containsExactly(rule);
    }

    private static RuleRepository repository(Rule... rules) {
        List<Rule> values = List.of(rules);
        return new RuleRepository() {
            @Override
            public Rule save(Rule rule) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Optional<Rule> findById(RuleId id) {
                return values.stream().filter(rule -> rule.id().equals(id)).findFirst();
            }

            @Override
            public List<Rule> findAll() {
                return new ArrayList<>(values);
            }

            @Override
            public List<Rule> findByType(RuleType type) {
                return values.stream().filter(rule -> rule.type() == type).toList();
            }

            @Override
            public List<Rule> findByStatus(RuleStatus status) {
                return values.stream().filter(rule -> rule.status() == status).toList();
            }

            @Override
            public boolean existsById(RuleId id) {
                return values.stream().anyMatch(rule -> rule.id().equals(id));
            }
        };
    }

    private static Rule rule(RuleStatus status) {
        return rule(RuleType.values()[0], status);
    }

    private static Rule rule(RuleType type, RuleStatus status) {
        return new Rule(
                RuleId.newId(),
                type,
                status,
                new RuleMetadata("Rule", "Description", Map.of()),
                "definition");
    }

    private static RuleType otherType(RuleType type) {
        return java.util.Arrays.stream(RuleType.values())
                .filter(candidate -> candidate != type)
                .findFirst()
                .orElse(type);
    }
}
