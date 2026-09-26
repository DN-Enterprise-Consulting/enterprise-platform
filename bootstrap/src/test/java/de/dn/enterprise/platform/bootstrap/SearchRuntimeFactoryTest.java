package de.dn.enterprise.platform.bootstrap;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class SearchRuntimeFactoryTest {
    @Test
    void createsFullyComposedRuntime() throws Exception {
        SearchRuntime runtime = SearchRuntimeFactory.create(
                new SearchRuntimeConfiguration(Files.createTempDirectory("search-runtime-test")));

        assertThat(runtime.persistence()).isNotNull();
        assertThat(runtime.repository()).isNotNull();
        assertThat(runtime.service()).isNotNull();
        assertThat(runtime.retrieval()).isNotNull();
    }

    @Test
    void defaultFactoryCreatesRuntime() {
        SearchRuntime runtime = SearchRuntimeFactory.createDefault();

        assertThat(runtime.service()).isNotNull();
        assertThat(runtime.retrieval()).isNotNull();
    }
}
