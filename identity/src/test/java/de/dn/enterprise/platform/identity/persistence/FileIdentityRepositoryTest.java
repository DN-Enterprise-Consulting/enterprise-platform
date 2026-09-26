package de.dn.enterprise.platform.identity.persistence;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityMetadata;
import de.dn.enterprise.platform.identity.domain.IdentityStatus;
import de.dn.enterprise.platform.identity.domain.IdentityType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileIdentityRepositoryTest {

    @TempDir
    Path tempDirectory;

    @Test
    void savesAndFindsById() {
        FileIdentityRepository repository = repository();
        Identity identity = identity(IdentityType.USER, IdentityStatus.ACTIVE, "User");

        assertThat(repository.save(identity)).isEqualTo(identity);
        assertThat(repository.findById(identity.id())).isEqualTo(identity);
        assertThat(repository.existsById(identity.id())).isTrue();
    }

    @Test
    void findsAllDeterministicallyById() {
        FileIdentityRepository repository = repository();
        Identity first = identity(IdentityType.USER, IdentityStatus.ACTIVE, "First");
        Identity second = identity(IdentityType.SERVICE, IdentityStatus.LOCKED, "Second");

        repository.save(first);
        repository.save(second);

        List<Identity> expected = List.of(first, second).stream()
                .sorted(Comparator.comparing(identity -> identity.id().value()))
                .toList();

        assertThat(repository.findAll()).containsExactlyElementsOf(expected);
    }

    @Test
    void filtersByType() {
        FileIdentityRepository repository = repository();
        Identity user = identity(IdentityType.USER, IdentityStatus.ACTIVE, "User");
        Identity service = identity(IdentityType.SERVICE, IdentityStatus.ACTIVE, "Service");

        repository.save(user);
        repository.save(service);

        assertThat(repository.findByType(IdentityType.USER))
                .containsExactly(user);
        assertThat(repository.findByType(IdentityType.SERVICE))
                .containsExactly(service);
    }

    @Test
    void filtersByStatus() {
        FileIdentityRepository repository = repository();
        Identity active = identity(IdentityType.USER, IdentityStatus.ACTIVE, "Active");
        Identity locked = identity(IdentityType.SERVICE, IdentityStatus.LOCKED, "Locked");

        repository.save(active);
        repository.save(locked);

        assertThat(repository.findByStatus(IdentityStatus.ACTIVE))
                .containsExactly(active);
        assertThat(repository.findByStatus(IdentityStatus.LOCKED))
                .containsExactly(locked);
    }

    @Test
    void survivesRepositoryRecreation() {
        Identity identity = identity(
                IdentityType.SERVICE,
                IdentityStatus.ACTIVE,
                "Persistent Service");

        FileIdentityPersistence persistence = new FileIdentityPersistence(tempDirectory);
        FileIdentityRepository first = new FileIdentityRepository(persistence);
        first.save(identity);

        FileIdentityRepository recreated =
                new FileIdentityRepository(new FileIdentityPersistence(tempDirectory));

        assertThat(recreated.findById(identity.id())).isEqualTo(identity);
        assertThat(recreated.findAll()).containsExactly(identity);
    }

    @Test
    void missingIdReturnsNull() {
        FileIdentityRepository repository = repository();

        assertThat(repository.findById(
                de.dn.enterprise.platform.identity.domain.IdentityId.random()))
                .isNull();
    }

    @Test
    void rejectsNullArguments() {
        FileIdentityRepository repository = repository();

        assertThatThrownBy(() -> repository.save(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findById(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findByType(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.findByStatus(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> repository.existsById(null))
                .isInstanceOf(NullPointerException.class);
    }

    private FileIdentityRepository repository() {
        return new FileIdentityRepository(
                new FileIdentityPersistence(tempDirectory));
    }

    private static Identity identity(
            IdentityType type,
            IdentityStatus status,
            String name) {
        return new Identity(
                de.dn.enterprise.platform.identity.domain.IdentityId.random(),
                type,
                status,
                IdentityMetadata.of(name, "Repository test identity"));
    }
}
