package de.dn.enterprise.platform.identity.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdentityDomainTest {

    @Test
    void createsActiveUserIdentityWithGeneratedId() {
        Identity identity = Identity.create(
                IdentityType.USER,
                IdentityMetadata.of("Alice", "Platform user"));

        assertThat(identity.id()).isNotNull();
        assertThat(identity.type()).isEqualTo(IdentityType.USER);
        assertThat(identity.status()).isEqualTo(IdentityStatus.ACTIVE);
    }

    @Test
    void identityIdRoundTripsFromString() {
        UUID uuid = UUID.randomUUID();

        IdentityId id = IdentityId.of(uuid.toString());

        assertThat(id.value()).isEqualTo(uuid);
        assertThat(id.toString()).isEqualTo(uuid.toString());
    }

    @Test
    void statusAndMetadataAreImmutableTransitions() {
        Identity identity = Identity.create(
                IdentityType.SERVICE,
                IdentityMetadata.of("worker", "Execution service"));

        Identity locked = identity.withStatus(IdentityStatus.LOCKED);
        Identity renamed = locked.withMetadata(
                IdentityMetadata.of("worker-v2", "Execution service v2"));

        assertThat(identity.status()).isEqualTo(IdentityStatus.ACTIVE);
        assertThat(locked.status()).isEqualTo(IdentityStatus.LOCKED);
        assertThat(renamed.status()).isEqualTo(IdentityStatus.LOCKED);
        assertThat(renamed.metadata().name()).isEqualTo("worker-v2");
        assertThat(renamed.id()).isEqualTo(identity.id());
    }

    @Test
    void rejectsBlankIdentityName() {
        assertThatThrownBy(() -> IdentityMetadata.of(" ", "description"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
