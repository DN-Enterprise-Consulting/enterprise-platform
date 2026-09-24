package de.dn.enterprise.platform.knowledge.persistence;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectMetadata;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectStatus;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.spi.KnowledgeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FileKnowledgeRepositoryTest {

    @TempDir
    Path tempDirectory;

    @Test
    void repositoryUsesPersistenceForSaveAndLoad() {
        FileKnowledgeRepository repository =
                new FileKnowledgeRepository(new FileKnowledgePersistence(tempDirectory));

        KnowledgeObject object = object(KnowledgeObjectType.STANDARD);

        repository.save(object);

        assertThat(repository.findById(object.id())).contains(object);
        assertThat(repository.existsById(object.id())).isTrue();
    }

    @Test
    void dataSurvivesRepositoryRecreation() {
        KnowledgeObject object = object(KnowledgeObjectType.RULE);

        new FileKnowledgeRepository(new FileKnowledgePersistence(tempDirectory)).save(object);

        KnowledgeRepository restarted =
                new FileKnowledgeRepository(new FileKnowledgePersistence(tempDirectory));

        assertThat(restarted.findById(object.id())).contains(object);
    }

    @Test
    void findAllAndFindByTypeUsePersistedData() {
        FileKnowledgeRepository repository =
                new FileKnowledgeRepository(new FileKnowledgePersistence(tempDirectory));

        KnowledgeObject standard = object(KnowledgeObjectType.STANDARD);
        KnowledgeObject rule = object(KnowledgeObjectType.RULE);

        repository.save(standard);
        repository.save(rule);

        assertThat(repository.findAll()).containsExactlyInAnyOrder(standard, rule);
        assertThat(repository.findByType(KnowledgeObjectType.RULE)).containsExactly(rule);
    }

    private KnowledgeObject object(KnowledgeObjectType type) {
        return KnowledgeObject.draft(
                type,
                new KnowledgeObjectMetadata(
                        "Repository Test",
                        "Repository persistence integration test",
                        Map.of("source", "m1.14")),
                "Persistent repository content");
    }
}
