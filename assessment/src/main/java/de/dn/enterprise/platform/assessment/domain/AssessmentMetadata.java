package de.dn.enterprise.platform.assessment.domain;

import java.util.Map;
import java.util.Objects;

/**
 * Descriptive metadata of an assessment.
 */
public record AssessmentMetadata(
        String name,
        String description,
        Map<String, String> attributes) {

    public AssessmentMetadata {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        description = description == null ? "" : description;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
