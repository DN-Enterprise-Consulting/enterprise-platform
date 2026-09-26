package de.dn.enterprise.platform.identity.spi;

import de.dn.enterprise.platform.identity.domain.Identity;
import de.dn.enterprise.platform.identity.domain.IdentityId;

/**
 * Technology-independent persistence contract for identities.
 *
 * Implementations are responsible for the concrete storage technology.
 */
public interface IdentityPersistence {

    void save(Identity identity);

    Identity load(IdentityId id);

    boolean exists(IdentityId id);

    void delete(IdentityId id);
}
