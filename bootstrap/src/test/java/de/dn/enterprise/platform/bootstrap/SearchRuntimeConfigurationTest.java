package de.dn.enterprise.platform.bootstrap;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class SearchRuntimeConfigurationTest {
    @Test
    void defaultsUseDataSearchDirectory() {
        assertThat(SearchRuntimeConfiguration.defaults().storageDirectory())
                .isEqualTo(Path.of("data", "search"));
    }

    @Test
    void nullStorageDirectoryIsRejected() {
        assertThatNullPointerException()
                .isThrownBy(() -> new SearchRuntimeConfiguration(null));
    }
}
