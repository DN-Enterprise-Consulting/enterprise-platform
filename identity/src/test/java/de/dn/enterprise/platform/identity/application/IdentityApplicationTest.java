package de.dn.enterprise.platform.identity.application;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityMetadata;
import de.dn.enterprise.platform.identity.domain.IdentityStatus;
import de.dn.enterprise.platform.identity.domain.IdentityType;
import de.dn.enterprise.platform.identity.inmemory.InMemoryIdentityRepository;
import de.dn.enterprise.platform.identity.query.IdentityQueryService;
import de.dn.enterprise.platform.identity.service.PersistentIdentityService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdentityApplicationTest {

    @Test
    void exposesStableCreateAndGetUseCases() {
        InMemoryIdentityRepository repository = new InMemoryIdentityRepository();
        PersistentIdentityService service = new PersistentIdentityService(repository);
        IdentityApplication application = new IdentityApplication(
                service, new IdentityQueryService(repository));

        Identity created = application.create(
                IdentityType.USER, IdentityMetadata.of("Alice", "Test user"));

        assertThat(application.get(created.id())).isEqualTo(created);
    }

    @Test
    void exposesQueryUseCases() {
        InMemoryIdentityRepository repository = new InMemoryIdentityRepository();
        PersistentIdentityService service = new PersistentIdentityService(repository);
        IdentityApplication application = new IdentityApplication(
                service, new IdentityQueryService(repository));

        Identity user = application.create(
                IdentityType.USER, IdentityMetadata.of("Alice", "User"));
        Identity serviceIdentity = application.create(
                IdentityType.SERVICE, IdentityMetadata.of("Orders", "Service"));
        Identity locked = application.lock(user.id());

        assertThat(application.findAll())
                .containsExactlyInAnyOrder(locked, serviceIdentity);
        assertThat(application.findByType(IdentityType.SERVICE))
                .containsExactly(serviceIdentity);
        assertThat(application.findByStatus(IdentityStatus.LOCKED))
                .containsExactly(locked);
        assertThat(application.findByTypeAndStatus(
                IdentityType.USER, IdentityStatus.LOCKED))
                .containsExactly(locked);
    }

    @Test
    void exposesLifecycleAndUpdateUseCases() {
        InMemoryIdentityRepository repository = new InMemoryIdentityRepository();
        PersistentIdentityService service = new PersistentIdentityService(repository);
        IdentityApplication application = new IdentityApplication(
                service, new IdentityQueryService(repository));

        Identity created = application.create(
                IdentityType.USER, IdentityMetadata.of("Old", "Description"));
        Identity updated = application.updateMetadata(
                created.id(), IdentityMetadata.of("New", "Description"));
        Identity locked = application.lock(updated.id());
        Identity active = application.activate(locked.id());
        Identity disabled = application.disable(active.id());

        assertThat(updated.metadata().name()).isEqualTo("New");
        assertThat(locked.status()).isEqualTo(IdentityStatus.LOCKED);
        assertThat(active.status()).isEqualTo(IdentityStatus.ACTIVE);
        assertThat(disabled.status()).isEqualTo(IdentityStatus.DISABLED);
    }

    @Test
    void rejectsMissingDependencies() {
        InMemoryIdentityRepository repository = new InMemoryIdentityRepository();
        PersistentIdentityService service = new PersistentIdentityService(repository);
        IdentityQueryService queryService = new IdentityQueryService(repository);

        assertThatThrownBy(() -> new IdentityApplication(null, queryService))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new IdentityApplication(service, null))
                .isInstanceOf(NullPointerException.class);
    }
}
