package de.dn.enterprise.platform.knowledge.domain;

import java.util.Objects;
import java.util.UUID;

public record KnowledgeObjectId(UUID value) {
    public KnowledgeObjectId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static KnowledgeObjectId newId() {
        return new KnowledgeObjectId(UUID.randomUUID());
    }

    public static KnowledgeObjectId of(UUID value) {
        return new KnowledgeObjectId(value);
    }

    public static KnowledgeObjectId parse(String value) {
        return new KnowledgeObjectId(UUID.fromString(
                Objects.requireNonNull(value, "value must not be null")));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
