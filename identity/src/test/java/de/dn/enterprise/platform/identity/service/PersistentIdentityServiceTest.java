package de.dn.enterprise.platform.identity.service;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityMetadata;
import de.dn.enterprise.platform.identity.domain.IdentityStatus;
import de.dn.enterprise.platform.identity.domain.IdentityType;
import de.dn.enterprise.platform.identity.inmemory.InMemoryIdentityRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersistentIdentityServiceTest {

    @Test
    void createsAndGetsIdentity() {
        PersistentIdentityService service =
                new PersistentIdentityService(new InMemoryIdentityRepository());

        Identity identity = service.create(
                IdentityType.USER,
                IdentityMetadata.of("Alice", "Test user"));

        assertThat(identity.status()).isEqualTo(IdentityStatus.ACTIVE);
        assertThat(service.get(identity.id())).isEqualTo(identity);
    }

    @Test
    void updatesMetadataWhileActive() {
        PersistentIdentityService service =
                new PersistentIdentityService(new InMemoryIdentityRepository());

        Identity identity = service.create(
                IdentityType.USER,
                IdentityMetadata.of("Before", "Description"));

        Identity updated = service.updateMetadata(
                identity.id(),
                IdentityMetadata.of("After", "Updated"));

        assertThat(updated.id()).isEqualTo(identity.id());
        assertThat(updated.type()).isEqualTo(identity.type());
        assertThat(updated.status()).isEqualTo(IdentityStatus.ACTIVE);
        assertThat(updated.metadata().name()).isEqualTo("After");
    }

    @Test
    void locksAndReactivatesIdentity() {
        PersistentIdentityService service =
                new PersistentIdentityService(new InMemoryIdentityRepository());

        Identity identity = service.create(
                IdentityType.SERVICE,
                IdentityMetadata.of("Service", "Test service"));

        Identity locked = service.lock(identity.id());
        assertThat(locked.status()).isEqualTo(IdentityStatus.LOCKED);

        Identity active = service.activate(identity.id());
        assertThat(active.status()).isEqualTo(IdentityStatus.ACTIVE);
    }

    @Test
    void disablesIdentityAndKeepsItTerminal() {
        PersistentIdentityService service =
                new PersistentIdentityService(new InMemoryIdentityRepository());

        Identity identity = service.create(
                IdentityType.USER,
                IdentityMetadata.of("User", "Test user"));

        Identity disabled = service.disable(identity.id());
        assertThat(disabled.status()).isEqualTo(IdentityStatus.DISABLED);

        assertThatThrownBy(() -> service.activate(identity.id()))
                .isInstanceOf(IdentityLifecycleException.class);

        assertThatThrownBy(() -> service.lock(identity.id()))
                .isInstanceOf(IdentityLifecycleException.class);

        assertThatThrownBy(() -> service.updateMetadata(
                identity.id(),
                IdentityMetadata.of("Changed", "Changed")))
                .isInstanceOf(IdentityLifecycleException.class);
    }

    @Test
    void supportsLockedToDisabledTransition() {
        PersistentIdentityService service =
                new PersistentIdentityService(new InMemoryIdentityRepository());

        Identity identity = service.create(
                IdentityType.USER,
                IdentityMetadata.of("User", "Test user"));

        service.lock(identity.id());
        Identity disabled = service.disable(identity.id());

        assertThat(disabled.status()).isEqualTo(IdentityStatus.DISABLED);
    }

    @Test
    void repeatedTargetTransitionIsIdempotent() {
        PersistentIdentityService service =
                new PersistentIdentityService(new InMemoryIdentityRepository());

        Identity identity = service.create(
                IdentityType.USER,
                IdentityMetadata.of("User", "Test user"));

        Identity locked = service.lock(identity.id());
        Identity lockedAgain = service.lock(identity.id());

        assertThat(lockedAgain).isEqualTo(locked);
    }

    @Test
    void missingIdentityIsRejected() {
        PersistentIdentityService service =
                new PersistentIdentityService(new InMemoryIdentityRepository());

        var id = de.dn.enterprise.platform.identity.domain.IdentityId.random();

        assertThatThrownBy(() -> service.get(id))
                .isInstanceOf(IdentityNotFoundException.class);
    }

    @Test
    void rejectsNullArguments() {
        PersistentIdentityService service =
                new PersistentIdentityService(new InMemoryIdentityRepository());

        assertThatThrownBy(() -> service.create(null, null))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> service.get(null))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> service.updateMetadata(null, null))
                .isInstanceOf(NullPointerException.class);
    }
}
