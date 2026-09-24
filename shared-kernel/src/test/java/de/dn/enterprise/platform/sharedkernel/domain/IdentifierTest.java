package de.dn.enterprise.platform.sharedkernel.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IdentifierTest {
    @Test
    void createsAndParsesIdentifier() {
        UUID uuid = UUID.randomUUID();
        Identifier identifier = Identifier.of(uuid.toString());

        assertThat(identifier.value()).isEqualTo(uuid);
        assertThat(identifier.toString()).isEqualTo(uuid.toString());
    }
}
