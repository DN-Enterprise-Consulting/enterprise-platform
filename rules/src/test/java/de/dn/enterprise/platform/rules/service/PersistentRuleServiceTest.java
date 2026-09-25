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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersistentRuleServiceTest {

    @TempDir
    Path tempDirectory;

    @Test
    void createsPersistsAndReloadsRule() {
        PersistentRuleService service = service();

        Rule created = service.create(
                RuleType.values()[0],
                metadata("Architecture Rule"),
                "java.version >= 21");

        PersistentRuleService restarted = service();

        assertThat(restarted.get(created.id())).isEqualTo(created);
    }

    @Test
    void updatesDefinitionAndMetadataPersistently() {
        PersistentRuleService service = service();

        Rule created = service.create(
                RuleType.values()[0],
                metadata("Initial"),
                "initial");

        Rule definitionUpdated = service.updateDefinition(created.id(), "updated");
        Rule metadataUpdated = service.updateMetadata(
                created.id(),
                metadata("Updated"));

        PersistentRuleService restarted = service();

        assertThat(definitionUpdated.definition()).isEqualTo("updated");
        assertThat(metadataUpdated.metadata().name()).isEqualTo("Updated");
        assertThat(restarted.get(created.id()).definition()).isEqualTo("updated");
        assertThat(restarted.get(created.id()).metadata().name()).isEqualTo("Updated");
        assertThat(restarted.get(created.id()).status()).isEqualTo(RuleStatus.DRAFT);
    }

    @Test
    void changesStatusPersistently() {
        PersistentRuleService service = service();

        Rule created = service.create(
                RuleType.values()[0],
                metadata("Lifecycle"),
                "definition");

        Rule active = service.activate(created.id());
        Rule deprecated = service.deprecate(active.id());
        Rule archived = service.archive(deprecated.id());

        PersistentRuleService restarted = service();

        assertThat(active.status()).isEqualTo(RuleStatus.ACTIVE);
        assertThat(deprecated.status()).isEqualTo(RuleStatus.DEPRECATED);
        assertThat(archived.status()).isEqualTo(RuleStatus.ARCHIVED);
        assertThat(restarted.get(created.id()).status()).isEqualTo(RuleStatus.ARCHIVED);
    }

    @Test
    void missingRuleIsRejected() {
        PersistentRuleService service = service();

        assertThatThrownBy(() -> service.get(RuleId.newId()))
                .isInstanceOf(RuleNotFoundException.class);
    }

    @Test
    void queriesUsePersistentRepository() {
        PersistentRuleService service = service();

        Rule first = service.create(
                RuleType.values()[0],
                metadata("First"),
                "first");

        assertThat(service.findAll()).containsExactly(first);
        assertThat(service.findByType(first.type())).containsExactly(first);
        assertThat(service.findByStatus(RuleStatus.DRAFT)).containsExactly(first);
    }

    private PersistentRuleService service() {
        return new PersistentRuleService(
                new FileRuleRepository(new FileRulePersistence(tempDirectory)));
    }

    private static RuleMetadata metadata(String name) {
        return new RuleMetadata(name, "description", Map.of("source", "test"));
    }
}
