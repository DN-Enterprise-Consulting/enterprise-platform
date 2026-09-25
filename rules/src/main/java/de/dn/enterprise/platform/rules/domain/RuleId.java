package de.dn.enterprise.platform.rules.domain;

import java.util.Objects;
import java.util.UUID;

public record RuleId(UUID value) {
    public RuleId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static RuleId newId() {
        return new RuleId(UUID.randomUUID());
    }

    public static RuleId of(UUID value) {
        return new RuleId(value);
    }

    public static RuleId parse(String value) {
        Objects.requireNonNull(value, "value must not be null");
        return new RuleId(UUID.fromString(value));
    }
}
