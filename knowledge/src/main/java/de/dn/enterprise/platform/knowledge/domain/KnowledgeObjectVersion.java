package de.dn.enterprise.platform.knowledge.domain;

import java.util.Objects;

public record KnowledgeObjectVersion(int major, int minor, int patch) {
    public KnowledgeObjectVersion {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("version components must not be negative");
        }
    }

    public static KnowledgeObjectVersion initial() {
        return new KnowledgeObjectVersion(1, 0, 0);
    }

    public KnowledgeObjectVersion nextMajor() {
        return new KnowledgeObjectVersion(major + 1, 0, 0);
    }

    public KnowledgeObjectVersion nextMinor() {
        return new KnowledgeObjectVersion(major, minor + 1, 0);
    }

    public KnowledgeObjectVersion nextPatch() {
        return new KnowledgeObjectVersion(major, minor, patch + 1);
    }

    @Override
    public String toString() {
        return "%d.%d.%d".formatted(major, minor, patch);
    }
}
