package de.dn.enterprise.platform.identity.spi;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdentityPersistenceContractTest {

    @Test
    void contractDefinesExpectedOperations() throws Exception {
        assertThat(IdentityPersistence.class.getMethod("save", Identity.class).getReturnType())
                .isEqualTo(void.class);

        assertThat(IdentityPersistence.class.getMethod("load", IdentityId.class).getReturnType())
                .isEqualTo(Identity.class);

        assertThat(IdentityPersistence.class.getMethod("exists", IdentityId.class).getReturnType())
                .isEqualTo(boolean.class);

        assertThat(IdentityPersistence.class.getMethod("delete", IdentityId.class).getReturnType())
                .isEqualTo(void.class);
    }
}
