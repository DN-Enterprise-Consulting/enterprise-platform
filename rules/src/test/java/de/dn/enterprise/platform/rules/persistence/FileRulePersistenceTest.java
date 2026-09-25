package de.dn.enterprise.platform.rules.persistence;

import de.dn.enterprise.platform.rules.domain.Rule;
import de.dn.enterprise.platform.rules.domain.RuleId;
import de.dn.enterprise.platform.rules.domain.RuleMetadata;
import de.dn.enterprise.platform.rules.domain.RuleStatus;
import de.dn.enterprise.platform.rules.domain.RuleType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileRulePersistenceTest {

    @TempDir
    Path tempDirectory;

    @Test
    void savesAndLoadsRuleWithCompleteState() {
        FileRulePersistence persistence = new FileRulePersistence(tempDirectory);
        Rule rule = rule();

        persistence.save(rule);

        assertThat(persistence.exists(rule.id())).isTrue();
        assertThat(persistence.load(rule.id())).contains(rule);
    }

    @Test
    void returnsEmptyForUnknownRule() {
        FileRulePersistence persistence = new FileRulePersistence(tempDirectory);

        assertThat(persistence.load(RuleId.newId())).isEmpty();
    }

    @Test
    void deleteRemovesRule() {
        FileRulePersistence persistence = new FileRulePersistence(tempDirectory);
        Rule rule = rule();

        persistence.save(rule);
        persistence.delete(rule.id());

        assertThat(persistence.exists(rule.id())).isFalse();
        assertThat(persistence.load(rule.id())).isEmpty();
    }

    @Test
    void saveReplacesExistingRuleWithSameId() {
        FileRulePersistence persistence = new FileRulePersistence(tempDirectory);
        Rule first = rule();
        Rule second = new Rule(
                first.id(),
                first.type(),
                RuleStatus.ACTIVE,
                new RuleMetadata("updated", "updated description", Map.of("version", "2")),
                "updated definition");

        persistence.save(first);
        persistence.save(second);

        assertThat(persistence.load(first.id())).contains(second);
    }

    @Test
    void rejectsNullArguments() {
        FileRulePersistence persistence = new FileRulePersistence(tempDirectory);

        assertThatThrownBy(() -> persistence.save(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistence.load(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistence.exists(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistence.delete(null))
                .isInstanceOf(NullPointerException.class);
    }

    private static Rule rule() {
        return new Rule(
                RuleId.newId(),
                RuleType.values()[0],
                RuleStatus.DRAFT,
                new RuleMetadata(
                        "Architecture Rule",
                        "Example rule",
                        Map.of("severity", "high", "owner", "platform")),
                "java.version >= 21");
    }
}
