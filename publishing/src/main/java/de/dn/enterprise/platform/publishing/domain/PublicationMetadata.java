package de.dn.enterprise.platform.publishing.domain;

import java.util.Map;
import java.util.Objects;

public record PublicationMetadata(
        String name,
        String description,
        Map<String, String> attributes) {

    public PublicationMetadata {
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        description = description == null ? "" : description;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static PublicationMetadata of(String name) {
        return new PublicationMetadata(name, "", Map.of());
    }
}
