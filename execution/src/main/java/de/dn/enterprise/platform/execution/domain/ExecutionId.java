package de.dn.enterprise.platform.execution.domain;

import java.util.Objects;
import java.util.UUID;

public record ExecutionId(UUID value) {

    public ExecutionId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static ExecutionId newId() {
        return new ExecutionId(UUID.randomUUID());
    }
}
