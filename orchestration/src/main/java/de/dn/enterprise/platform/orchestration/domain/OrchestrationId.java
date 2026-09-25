package de.dn.enterprise.platform.orchestration.domain;
import java.util.Objects;
import java.util.UUID;
public record OrchestrationId(UUID value) {
    public OrchestrationId { Objects.requireNonNull(value, "value must not be null"); }
    public static OrchestrationId newId() { return new OrchestrationId(UUID.randomUUID()); }
}
