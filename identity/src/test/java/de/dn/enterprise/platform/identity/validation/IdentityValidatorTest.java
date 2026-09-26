package de.dn.enterprise.platform.identity.validation;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityMetadata;
import de.dn.enterprise.platform.identity.domain.IdentityStatus;
import de.dn.enterprise.platform.identity.domain.IdentityType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdentityValidatorTest {
    private final IdentityValidator validator = new IdentityValidator();

    @Test
    void acceptsValidIdentity() {
        Identity identity = Identity.create(IdentityType.USER, IdentityMetadata.of("Alice", "Test user"));
        assertThatCode(() -> validator.validate(identity)).doesNotThrowAnyException();
    }

    @Test
    void rejectsNullIdentity() {
        assertThatThrownBy(() -> validator.validate(null)).isInstanceOf(IdentityValidationException.class);
    }

    @Test
    void domainRejectsBlankMetadataName() {
        assertThatThrownBy(() -> IdentityMetadata.of(" ", "Description"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void acceptsAllSupportedStatuses() {
        for (IdentityStatus status : IdentityStatus.values()) {
            Identity identity = new Identity(
                    de.dn.enterprise.platform.identity.domain.IdentityId.random(),
                    IdentityType.SERVICE,
                    status,
                    IdentityMetadata.of("Service", "Description"));
            assertThatCode(() -> validator.validate(identity)).doesNotThrowAnyException();
        }
    }
}
