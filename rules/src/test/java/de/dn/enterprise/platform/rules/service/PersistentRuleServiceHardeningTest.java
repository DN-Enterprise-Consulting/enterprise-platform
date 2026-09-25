package de.dn.enterprise.platform.rules.service;

import de.dn.enterprise.platform.rules.domain.Rule;
import de.dn.enterprise.platform.rules.domain.RuleId;
import de.dn.enterprise.platform.rules.domain.RuleMetadata;
import de.dn.enterprise.platform.rules.domain.RuleStatus;
import de.dn.enterprise.platform.rules.domain.RuleType;
import de.dn.enterprise.platform.rules.persistence.FileRulePersistence;
import de.dn.enterprise.platform.rules.persistence.FileRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersistentRuleServiceHardeningTest {

    @TempDir
    Path tempDirectory;

    @Test
    void lifecycleCannotSkipStatesThroughPersistentService() {
        PersistentRuleService service = service();

        Rule created = service.create(
                RuleType.values()[0],
                new RuleMetadata("Rule", "Description", Map.of()),
                "definition");

        assertThatThrownBy(() -> service.deprecate(created.id()))
                .isInstanceOf(RuleLifecycleException.class);

        assertThatThrownBy(() -> service.archive(created.id()))
                .isInstanceOf(RuleLifecycleException.class);
    }

    @Test
    void archivedRuleCannotBeChanged() {
        PersistentRuleService service = service();

        Rule created = service.create(
                RuleType.values()[0],
                new RuleMetadata("Rule", "Description", Map.of()),
                "definition");

        service.activate(created.id());
        service.deprecate(created.id());
        service.archive(created.id());

        assertThatThrownBy(() -> service.updateDefinition(created.id(), "changed"))
                .isInstanceOf(RuleLifecycleException.class);

        assertThatThrownBy(() -> service.updateMetadata(
                created.id(),
                new RuleMetadata("Changed", "Description", Map.of())))
                .isInstanceOf(RuleLifecycleException.class);
    }

    private PersistentRuleService service() {
        return new PersistentRuleService(
                new FileRuleRepository(new FileRulePersistence(tempDirectory)));
    }
}
