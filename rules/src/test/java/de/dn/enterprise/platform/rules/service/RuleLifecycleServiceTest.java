package de.dn.enterprise.platform.rules.service;

import de.dn.enterprise.platform.rules.domain.Rule;
import de.dn.enterprise.platform.rules.domain.RuleId;
import de.dn.enterprise.platform.rules.domain.RuleMetadata;
import de.dn.enterprise.platform.rules.domain.RuleStatus;
import de.dn.enterprise.platform.rules.domain.RuleType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RuleLifecycleServiceTest {

    private final RuleLifecycleService lifecycle = new RuleLifecycleService();

    @Test
    void allowsLifecycleSequence() {
        Rule draft = rule(RuleStatus.DRAFT);

        Rule active = lifecycle.transition(draft, RuleStatus.ACTIVE);
        Rule deprecated = lifecycle.transition(active, RuleStatus.DEPRECATED);
        Rule archived = lifecycle.transition(deprecated, RuleStatus.ARCHIVED);

        assertThat(active.status()).isEqualTo(RuleStatus.ACTIVE);
        assertThat(deprecated.status()).isEqualTo(RuleStatus.DEPRECATED);
        assertThat(archived.status()).isEqualTo(RuleStatus.ARCHIVED);
    }

    @Test
    void rejectsSkippingLifecycleStates() {
        Rule draft = rule(RuleStatus.DRAFT);

        assertThatThrownBy(() -> lifecycle.transition(draft, RuleStatus.DEPRECATED))
                .isInstanceOf(RuleLifecycleException.class);

        assertThatThrownBy(() -> lifecycle.transition(draft, RuleStatus.ARCHIVED))
                .isInstanceOf(RuleLifecycleException.class);
    }

    @Test
    void rejectsTransitionsOutOfArchived() {
        Rule archived = rule(RuleStatus.ARCHIVED);

        assertThatThrownBy(() -> lifecycle.transition(archived, RuleStatus.DRAFT))
                .isInstanceOf(RuleLifecycleException.class);

        assertThatThrownBy(() -> lifecycle.transition(archived, RuleStatus.ACTIVE))
                .isInstanceOf(RuleLifecycleException.class);
    }

    @Test
    void sameStatusIsIdempotent() {
        Rule active = rule(RuleStatus.ACTIVE);

        assertThat(lifecycle.transition(active, RuleStatus.ACTIVE))
                .isSameAs(active);
    }

    private static Rule rule(RuleStatus status) {
        return new Rule(
                RuleId.newId(),
                RuleType.values()[0],
                status,
                new RuleMetadata("Rule", "Description", Map.of()),
                "definition");
    }
}
