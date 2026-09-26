package de.dn.enterprise.platform.identity.inmemory;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityMetadata;
import de.dn.enterprise.platform.identity.domain.IdentityStatus;
import de.dn.enterprise.platform.identity.domain.IdentityType;
import de.dn.enterprise.platform.identity.spi.IdentityRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryIdentityRepositoryTest {

    private final IdentityRepository repository = new InMemoryIdentityRepository();

    @Test
    void savesAndFindsById() {
        Identity identity = identity("alpha", IdentityType.USER, IdentityStatus.ACTIVE);

        assertThat(repository.save(identity)).isSameAs(identity);
        assertThat(repository.findById(identity.id())).isSameAs(identity);
        assertThat(repository.existsById(identity.id())).isTrue();
    }

    @Test
    void replacesIdentityWithSameId() {
        Identity original = identity("alpha", IdentityType.USER, IdentityStatus.ACTIVE);
        Identity replacement = new Identity(
                original.id(),
                IdentityType.SERVICE,
                IdentityStatus.LOCKED,
                new IdentityMetadata("replacement", "replacement identity")
        );

        repository.save(original);
        repository.save(replacement);

        assertThat(repository.findById(original.id())).isSameAs(replacement);
        assertThat(repository.findAll()).containsExactly(replacement);
    }

    @Test
    void findsAllDeterministicallyById() {
        Identity first = identity("zeta", IdentityType.USER, IdentityStatus.ACTIVE);
        Identity second = identity("alpha", IdentityType.SERVICE, IdentityStatus.ACTIVE);

        repository.save(first);
        repository.save(second);

        List<Identity> expected = List.of(first, second).stream()
                .sorted(Comparator.comparing(identity -> identity.id().value()))
                .toList();

        assertThat(repository.findAll()).containsExactlyElementsOf(expected);
    }

    @Test
    void filtersByTypeAndStatus() {
        Identity user = identity("user", IdentityType.USER, IdentityStatus.ACTIVE);
        Identity service = identity("service", IdentityType.SERVICE, IdentityStatus.LOCKED);

        repository.save(user);
        repository.save(service);

        assertThat(repository.findByType(IdentityType.USER)).containsExactly(user);
        assertThat(repository.findByType(IdentityType.SERVICE)).containsExactly(service);
        assertThat(repository.findByStatus(IdentityStatus.ACTIVE)).containsExactly(user);
        assertThat(repository.findByStatus(IdentityStatus.LOCKED)).containsExactly(service);
    }

    @Test
    void rejectsNullInputs() {
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

    private Identity identity(String name, IdentityType type, IdentityStatus status) {
        return new Identity(
                de.dn.enterprise.platform.identity.domain.IdentityId.random(),
                type,
                status,
                new IdentityMetadata(name, "test identity")
        );
    }
}
