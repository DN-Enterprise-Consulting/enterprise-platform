package de.dn.enterprise.platform.bootstrap;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Immutable runtime configuration for the Knowledge subsystem.
 */
public record KnowledgeRuntimeConfiguration(Path storageDirectory) {

    public KnowledgeRuntimeConfiguration {
        Objects.requireNonNull(storageDirectory, "storageDirectory must not be null");
    }

    public static KnowledgeRuntimeConfiguration fileBacked(Path storageDirectory) {
        return new KnowledgeRuntimeConfiguration(storageDirectory);
    }
}
