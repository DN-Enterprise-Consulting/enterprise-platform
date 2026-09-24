package de.dn.enterprise.platform.bootstrap;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeRuntimeTest {

    @TempDir
    Path tempDirectory;

    @Test
    void createsFileBackedKnowledgeRuntimeFromConfiguration() {
        KnowledgeRuntimeConfiguration configuration =
                KnowledgeRuntimeConfiguration.fileBacked(tempDirectory);

        KnowledgeRuntime runtime = KnowledgeRuntime.create(configuration);

        KnowledgeObject object = runtime.knowledgeService().create(
                KnowledgeObjectType.STANDARD,
                "Configuration Test",
                "Runtime configuration",
                "Configuration content",
                Map.of("source", "m1.17"));

        assertThat(runtime.knowledgeService().get(object.id()))
                .isEqualTo(object);
    }

    @Test
    void configurationKeepsPersistenceIndependentFromRuntimeConstruction() {
        KnowledgeRuntimeConfiguration configuration =
                KnowledgeRuntimeConfiguration.fileBacked(tempDirectory);

        KnowledgeRuntime first = KnowledgeRuntime.create(configuration);

        KnowledgeObject object = first.knowledgeService().create(
                KnowledgeObjectType.RULE,
                "Persistent Configuration Test",
                "Runtime recreation",
                "Persistent content",
                Map.of());

        KnowledgeRuntime second = KnowledgeRuntime.create(configuration);

        assertThat(second.knowledgeService().get(object.id()))
                .isEqualTo(object);
    }
}
