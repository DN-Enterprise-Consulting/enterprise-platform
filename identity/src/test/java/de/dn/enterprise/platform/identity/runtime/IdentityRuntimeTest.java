package de.dn.enterprise.platform.identity.runtime;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityMetadata;
import de.dn.enterprise.platform.identity.domain.IdentityStatus;
import de.dn.enterprise.platform.identity.domain.IdentityType;
import de.dn.enterprise.platform.identity.query.IdentityQuery;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdentityRuntimeTest {

    @Test
    void fileBackedRuntimeComposesAllIdentityComponents() throws Exception {
        Path storage = Files.createTempDirectory("identity-runtime-");

        IdentityRuntime runtime = IdentityRuntime.fileBacked(storage);
        Identity identity = runtime.identityService().create(
                IdentityType.USER,
                IdentityMetadata.of("Alice", "Runtime test"));

        assertThat(runtime.persistence()).isNotNull();
        assertThat(runtime.repository()).isNotNull();
        assertThat(runtime.validator()).isNotNull();
        assertThat(runtime.identityService().get(identity.id())).isEqualTo(identity);
        assertThat(runtime.queryService().find(IdentityQuery.byType(IdentityType.USER)))
                .containsExactly(identity);
        assertThat(Files.exists(storage.resolve(identity.id().value() + ".identity"))).isTrue();
    }

    @Test
    void runtimeSurvivesRecreationAndReloadsPersistentIdentity() throws Exception {
        Path storage = Files.createTempDirectory("identity-runtime-restart-");

        IdentityRuntime first = IdentityRuntime.fileBacked(storage);
        Identity created = first.identityService().create(
                IdentityType.SERVICE,
                IdentityMetadata.of("Runtime Service", "Persistent identity"));

        IdentityRuntime restarted = IdentityRuntime.fileBacked(storage);

        assertThat(restarted.identityService().get(created.id())).isEqualTo(created);
        assertThat(restarted.queryService().findAll()).containsExactly(created);
    }

    @Test
    void runtimeUsesSharedStorageAcrossLifecycleAndQueryBoundaries() throws Exception {
        Path storage = Files.createTempDirectory("identity-runtime-lifecycle-");

        IdentityRuntime runtime = IdentityRuntime.fileBacked(storage);
        Identity identity = runtime.identityService().create(
                IdentityType.USER,
                IdentityMetadata.of("Bob", "Lifecycle test"));

        Identity locked = runtime.identityService().lock(identity.id());

        assertThat(locked.status()).isEqualTo(IdentityStatus.LOCKED);
        assertThat(runtime.queryService()
                .find(IdentityQuery.byStatus(IdentityStatus.LOCKED)))
                .containsExactly(locked);
    }

    @Test
    void rejectsNullStorageDirectory() {
        assertThatThrownBy(() -> IdentityRuntime.fileBacked(null))
                .isInstanceOf(NullPointerException.class);
    }
}
