package de.dn.enterprise.platform.identity.service;

import de.dn.enterprise.platform.identity.domain.IdentityId;

public final class IdentityNotFoundException extends RuntimeException {

    public IdentityNotFoundException(IdentityId id) {
        super("Identity not found: " + id.value());
    }
}
