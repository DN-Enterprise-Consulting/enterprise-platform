package de.dn.enterprise.platform.knowledge.service;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectStatus;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.persistence.FileKnowledgePersistence;
import de.dn.enterprise.platform.knowledge.persistence.FileKnowledgeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PersistentKnowledgeServiceIntegrationTest {

    @TempDir
    Path tempDirectory;

    @Test
    void completeLifecycleIsPersistedAcrossServiceRecreation() {
        PersistentKnowledgeService service = service();

        KnowledgeObject created = service.create(
                KnowledgeObjectType.STANDARD,
                "Persistent Standard",
                "End-to-end integration test",
                "Version one",
                Map.of("source", "m1.15"));

        assertThat(created.status()).isEqualTo(KnowledgeObjectStatus.DRAFT);

        KnowledgeObject active = service.activate(created.id());
        assertThat(active.status()).isEqualTo(KnowledgeObjectStatus.ACTIVE);

        KnowledgeObject updated = service.updateContent(created.id(), "Version two");
        assertThat(updated.version().patch()).isEqualTo(1);
        assertThat(updated.status()).isEqualTo(KnowledgeObjectStatus.ACTIVE);

        KnowledgeObject deprecated = service.deprecate(created.id());
        assertThat(deprecated.status()).isEqualTo(KnowledgeObjectStatus.DEPRECATED);

        KnowledgeObject archived = service.archive(created.id());
        assertThat(archived.status()).isEqualTo(KnowledgeObjectStatus.ARCHIVED);

        PersistentKnowledgeService restarted = service();

        assertThat(restarted.get(created.id())).isEqualTo(archived);
        assertThat(restarted.findByType(KnowledgeObjectType.STANDARD))
                .containsExactly(archived);
    }

    @Test
    void serviceReadsExistingPersistentData() {
        PersistentKnowledgeService first = service();

        KnowledgeObject object = first.create(
                KnowledgeObjectType.RULE,
                "Existing Rule",
                "Stored before service recreation",
                "Rule content",
                Map.of());

        PersistentKnowledgeService second = service();

        assertThat(second.get(object.id())).isEqualTo(object);
    }

    private PersistentKnowledgeService service() {
        return new PersistentKnowledgeService(
                new FileKnowledgeRepository(
                        new FileKnowledgePersistence(tempDirectory)));
    }
}
