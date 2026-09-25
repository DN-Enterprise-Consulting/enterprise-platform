package de.dn.enterprise.platform.rules.domain;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RuleTest {

    @Test
    void createsDraftRuleWithGeneratedId() {
        Rule rule = Rule.draft(
                RuleType.ARCHITECTURE,
                new RuleMetadata("Java baseline", "Architecture rule", Map.of("severity", "HIGH")),
                "java.version >= 21");

        assertThat(rule.id()).isNotNull();
        assertThat(rule.type()).isEqualTo(RuleType.ARCHITECTURE);
        assertThat(rule.status()).isEqualTo(RuleStatus.DRAFT);
        assertThat(rule.metadata().name()).isEqualTo("Java baseline");
        assertThat(rule.definition()).isEqualTo("java.version >= 21");
    }

    @Test
    void supportsImmutableChanges() {
        Rule original = Rule.draft(
                RuleType.QUALITY,
                new RuleMetadata("Quality", "", Map.of()),
                "coverage >= 70");

        Rule updated = original
                .withStatus(RuleStatus.ACTIVE)
                .withDefinition("coverage >= 80");

        assertThat(original.status()).isEqualTo(RuleStatus.DRAFT);
        assertThat(original.definition()).isEqualTo("coverage >= 70");
        assertThat(updated.status()).isEqualTo(RuleStatus.ACTIVE);
        assertThat(updated.definition()).isEqualTo("coverage >= 80");
        assertThat(updated.id()).isEqualTo(original.id());
    }

    @Test
    void rejectsBlankRuleName() {
        assertThatThrownBy(() -> new RuleMetadata(" ", "", Map.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsBlankDefinition() {
        RuleMetadata metadata = new RuleMetadata("Rule", "", Map.of());

        assertThatThrownBy(() -> Rule.draft(RuleType.CUSTOM, metadata, " "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
