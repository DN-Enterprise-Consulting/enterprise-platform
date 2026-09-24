package de.dn.enterprise.platform.bootstrap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class KnowledgeRuntimeConfigurationTest {

    @TempDir
    Path tempDirectory;

    @Test
    void storesStorageDirectory() {
        KnowledgeRuntimeConfiguration configuration =
                KnowledgeRuntimeConfiguration.fileBacked(tempDirectory);

        assertThat(configuration.storageDirectory()).isEqualTo(tempDirectory);
    }

    @Test
    void rejectsNullStorageDirectory() {
        assertThatNullPointerException()
                .isThrownBy(() -> new KnowledgeRuntimeConfiguration(null));
    }
}
