package de.dn.enterprise.platform.rules.domain;

import java.util.Objects;

public record Rule(
        RuleId id,
        RuleType type,
        RuleStatus status,
        RuleMetadata metadata,
        String definition) {

    public Rule {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(metadata, "metadata must not be null");
        Objects.requireNonNull(definition, "definition must not be null");
        if (definition.isBlank()) {
            throw new IllegalArgumentException("definition must not be blank");
        }
    }

    public static Rule draft(RuleType type, RuleMetadata metadata, String definition) {
        return new Rule(RuleId.newId(), type, RuleStatus.DRAFT, metadata, definition);
    }

    public Rule withStatus(RuleStatus newStatus) {
        return new Rule(id, type, newStatus, metadata, definition);
    }

    public Rule withMetadata(RuleMetadata newMetadata) {
        return new Rule(id, type, status, newMetadata, definition);
    }

    public Rule withDefinition(String newDefinition) {
        return new Rule(id, type, status, metadata, newDefinition);
    }
}
