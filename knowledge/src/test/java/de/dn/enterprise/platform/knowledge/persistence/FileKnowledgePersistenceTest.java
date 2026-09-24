package de.dn.enterprise.platform.knowledge.persistence;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectMetadata;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectStatus;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FileKnowledgePersistenceTest {

    @TempDir
    Path tempDirectory;

    @Test
    void savesAndLoadsCompleteKnowledgeObject() {
        FileKnowledgePersistence persistence = new FileKnowledgePersistence(tempDirectory);
        KnowledgeObject object = object();

        persistence.save(object);

        assertThat(persistence.exists(object.id())).isTrue();
        assertThat(persistence.load(object.id())).contains(object);
    }

    @Test
    void persistsAcrossNewPersistenceInstance() {
        KnowledgeObject object = object();

        new FileKnowledgePersistence(tempDirectory).save(object);

        FileKnowledgePersistence restartedPersistence =
                new FileKnowledgePersistence(tempDirectory);

        assertThat(restartedPersistence.load(object.id())).contains(object);
    }

    @Test
    void deletesPersistedObject() {
        FileKnowledgePersistence persistence = new FileKnowledgePersistence(tempDirectory);
        KnowledgeObject object = object();

        persistence.save(object);
        persistence.delete(object.id());

        assertThat(persistence.exists(object.id())).isFalse();
        assertThat(persistence.load(object.id())).isEmpty();
    }

    @Test
    void missingObjectReturnsEmpty() {
        FileKnowledgePersistence persistence = new FileKnowledgePersistence(tempDirectory);

        assertThat(persistence.load(KnowledgeObjectId.newId())).isEmpty();
    }

    @Test
    void createsRootDirectoryOnFirstSave() throws Exception {
        Path root = tempDirectory.resolve("knowledge-store");
        FileKnowledgePersistence persistence = new FileKnowledgePersistence(root);

        persistence.save(object());

        assertThat(Files.isDirectory(root)).isTrue();
        assertThat(Files.list(root).count()).isEqualTo(1);
    }

    private KnowledgeObject object() {
        return new KnowledgeObject(
                KnowledgeObjectId.newId(),
                KnowledgeObjectType.STANDARD,
                new KnowledgeObjectVersion(2, 3, 4),
                KnowledgeObjectStatus.ACTIVE,
                new KnowledgeObjectMetadata(
                        "Persistence Test",
                        "Durable knowledge object",
                        Map.of("owner", "platform", "scope", "enterprise")),
                "Line 1\nLine 2\nUnicode: äöü €");
    }
}
