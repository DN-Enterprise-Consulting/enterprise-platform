package de.dn.enterprise.platform.identity.service;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityId;
import de.dn.enterprise.platform.identity.domain.IdentityMetadata;
import de.dn.enterprise.platform.identity.domain.IdentityType;
import de.dn.enterprise.platform.identity.inmemory.InMemoryIdentityRepository;
import de.dn.enterprise.platform.identity.spi.IdentityRepository;
import de.dn.enterprise.platform.identity.validation.IdentityValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersistentIdentityServiceHardeningTest {
    @Test
    void rejectsNullValidator() {
        assertThatThrownBy(() -> new PersistentIdentityService(new InMemoryIdentityRepository(), null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void validatesRepositoryResultAfterSave() {
        IdentityRepository repository = new IdentityRepository() {
            public Identity save(Identity identity) { return null; }
            public Identity findById(IdentityId id) { return null; }
            public java.util.List<Identity> findAll() { return java.util.List.of(); }
            public java.util.List<Identity> findByType(IdentityType type) { return java.util.List.of(); }
            public java.util.List<Identity> findByStatus(de.dn.enterprise.platform.identity.domain.IdentityStatus status) { return java.util.List.of(); }
            public boolean existsById(IdentityId id) { return false; }
        };
        PersistentIdentityService service = new PersistentIdentityService(repository);
        assertThatThrownBy(() -> service.create(IdentityType.USER, IdentityMetadata.of("Alice", "Test user")))
                .isInstanceOf(IdentityValidationException.class);
    }
}
