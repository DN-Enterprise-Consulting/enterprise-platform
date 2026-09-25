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

class FileRuleRepositoryTest {

    @TempDir
    Path tempDirectory;

    @Test
    void persistsAndReloadsRulesThroughRepository() {
        FileRulePersistence persistence = new FileRulePersistence(tempDirectory);
        FileRuleRepository repository = new FileRuleRepository(persistence);
        Rule rule = rule();

        repository.save(rule);

        FileRuleRepository restartedRepository =
                new FileRuleRepository(new FileRulePersistence(tempDirectory));

        assertThat(restartedRepository.findById(rule.id())).contains(rule);
        assertThat(restartedRepository.existsById(rule.id())).isTrue();
        assertThat(restartedRepository.findAll()).containsExactly(rule);
    }

    @Test
    void findsByTypeAndStatusFromPersistentStorage() {
        FileRulePersistence persistence = new FileRulePersistence(tempDirectory);
        FileRuleRepository repository = new FileRuleRepository(persistence);

        Rule first = rule();
        Rule second = new Rule(
                RuleId.newId(),
                first.type(),
                RuleStatus.ACTIVE,
                new RuleMetadata("Second", "Second rule", Map.of()),
                "second definition");

        repository.save(first);
        repository.save(second);

        assertThat(repository.findByType(first.type())).containsExactlyInAnyOrder(first, second);
        assertThat(repository.findByStatus(RuleStatus.ACTIVE)).containsExactly(second);
        assertThat(repository.findByStatus(RuleStatus.DRAFT)).containsExactly(first);
    }

    @Test
    void deleteThroughPersistenceRemovesRepositoryEntry() {
        FileRulePersistence persistence = new FileRulePersistence(tempDirectory);
        FileRuleRepository repository = new FileRuleRepository(persistence);
        Rule rule = rule();

        repository.save(rule);
        persistence.delete(rule.id());

        assertThat(repository.findById(rule.id())).isEmpty();
        assertThat(repository.existsById(rule.id())).isFalse();
    }

    @Test
    void rejectsNullArguments() {
        FileRuleRepository repository =
                new FileRuleRepository(new FileRulePersistence(tempDirectory));

        assertThatThrownBy(() -> repository.save(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findById(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findByType(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findByStatus(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.existsById(null))
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
                        Map.of("severity", "high")),
                "java.version >= 21");
    }
}
