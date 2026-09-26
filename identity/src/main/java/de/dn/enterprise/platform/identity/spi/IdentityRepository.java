package de.dn.enterprise.platform.identity.spi;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityId;
import de.dn.enterprise.platform.identity.domain.IdentityStatus;
import de.dn.enterprise.platform.identity.domain.IdentityType;

import java.util.List;

/**
 * Persistence-agnostic repository contract for identities.
 */
public interface IdentityRepository {

    Identity save(Identity identity);

    Identity findById(IdentityId id);

    List<Identity> findAll();

    List<Identity> findByType(IdentityType type);

    List<Identity> findByStatus(IdentityStatus status);

    boolean existsById(IdentityId id);
}
