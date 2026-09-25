package de.dn.enterprise.platform.orchestration.domain;
import java.util.Map;
import java.util.Objects;
public record OrchestrationMetadata(String name, String description, Map<String,String> attributes) {
    public OrchestrationMetadata {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        Objects.requireNonNull(attributes, "attributes must not be null");
        attributes = Map.copyOf(attributes);
    }
}
