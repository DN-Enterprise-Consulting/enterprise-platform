package de.dn.enterprise.platform.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;

import org.junit.jupiter.api.Test;

class KnowledgeRuntimeFactoryTest {

    @Test
    void createsRuntimeFromConfiguration() throws Exception {
        var directory = Files.createTempDirectory("knowledge-runtime-factory");
        var configuration = KnowledgeRuntimeConfiguration.fileBacked(directory);

        var runtime = KnowledgeRuntimeFactory.create(configuration);

        assertThat(runtime).isNotNull();
    }

    @Test
    void rejectsNullConfiguration() {
        assertThatThrownBy(() -> KnowledgeRuntimeFactory.create(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("configuration must not be null");
    }
}
