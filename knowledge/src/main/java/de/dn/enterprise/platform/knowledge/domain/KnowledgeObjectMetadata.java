package de.dn.enterprise.platform.knowledge.domain;

import java.util.Map;
import java.util.Objects;

public record KnowledgeObjectMetadata(
        String name,
        String description,
        Map<String, String> attributes) {

    public KnowledgeObjectMetadata {
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        description = description == null ? "" : description;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
