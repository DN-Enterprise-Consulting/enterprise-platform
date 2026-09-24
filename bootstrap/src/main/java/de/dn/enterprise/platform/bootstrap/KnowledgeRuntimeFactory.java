package de.dn.enterprise.platform.bootstrap;

import java.util.Objects;

/**
 * Explicit composition boundary for creating the knowledge runtime.
 */
public final class KnowledgeRuntimeFactory {

    private KnowledgeRuntimeFactory() {
    }

    public static KnowledgeRuntime create(KnowledgeRuntimeConfiguration configuration) {
        Objects.requireNonNull(configuration, "configuration must not be null");
        return KnowledgeRuntime.create(configuration);
    }
}
