package de.dn.enterprise.platform.execution.runtime;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Immutable configuration for the execution runtime composition root.
 */
public record ExecutionRuntimeConfiguration(Path storageDirectory) {

    public ExecutionRuntimeConfiguration {
        Objects.requireNonNull(storageDirectory, "storageDirectory must not be null");
    }

    public static ExecutionRuntimeConfiguration fileBacked(Path storageDirectory) {
        return new ExecutionRuntimeConfiguration(storageDirectory);
    }
}
