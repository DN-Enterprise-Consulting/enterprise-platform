package de.dn.enterprise.platform.identity.spi;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityId;
import de.dn.enterprise.platform.identity.domain.IdentityMetadata;
import de.dn.enterprise.platform.identity.domain.IdentityType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IdentityRepositoryContractTest {

    @Test
    void contractDefinesExpectedOperations() throws Exception {
        assertThat(IdentityRepository.class.getMethod("save", Identity.class).getReturnType())
                .isEqualTo(Identity.class);

        assertThat(IdentityRepository.class.getMethod("findById", IdentityId.class).getReturnType())
                .isEqualTo(Identity.class);

        assertThat(IdentityRepository.class.getMethod("findAll").getReturnType())
                .isEqualTo(List.class);

        assertThat(IdentityRepository.class.getMethod("findByType", IdentityType.class).getReturnType())
                .isEqualTo(List.class);

        assertThat(IdentityRepository.class.getMethod("findByStatus",
                de.dn.enterprise.platform.identity.domain.IdentityStatus.class).getReturnType())
                .isEqualTo(List.class);

        assertThat(IdentityRepository.class.getMethod("existsById", IdentityId.class).getReturnType())
                .isEqualTo(boolean.class);
    }
}
