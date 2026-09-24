package de.dn.enterprise.platform.knowledge.persistence;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectMetadata;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.spi.KnowledgeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FileKnowledgeRepositoryContractTest {

    @TempDir
    Path tempDirectory;

    @Test
    void implementsRepositoryContract() {
        KnowledgeRepository repository =
                new FileKnowledgeRepository(new FileKnowledgePersistence(tempDirectory));

        KnowledgeObject object = KnowledgeObject.draft(
                KnowledgeObjectType.ASSET,
                new KnowledgeObjectMetadata(
                        "Contract Test",
                        "Repository contract",
                        Map.of()),
                "content");

        repository.save(object);

        assertThat(repository.findById(object.id())).contains(object);
        assertThat(repository.findByType(KnowledgeObjectType.ASSET)).containsExactly(object);
        assertThat(repository.findAll()).containsExactly(object);
        assertThat(repository.existsById(object.id())).isTrue();
    }
}
