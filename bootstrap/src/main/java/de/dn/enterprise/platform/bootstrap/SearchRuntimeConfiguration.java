package de.dn.enterprise.platform.bootstrap;

import java.nio.file.Path;
import java.util.Objects;

/** Immutable configuration for the Search runtime composition. */
public record SearchRuntimeConfiguration(Path storageDirectory) {

    public SearchRuntimeConfiguration {
        Objects.requireNonNull(storageDirectory, "storageDirectory must not be null");
    }

    public static SearchRuntimeConfiguration defaults() {
        return new SearchRuntimeConfiguration(Path.of("data", "search"));
    }
}
