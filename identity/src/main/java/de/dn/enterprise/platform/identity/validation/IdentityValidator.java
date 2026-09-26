package de.dn.enterprise.platform.identity.validation;

import de.dn.enterprise.platform.identity.domain.Identity;

/** Validates identity aggregates at application and persistence boundaries. */
public final class IdentityValidator {
    public void validate(Identity identity) {
        if (identity == null) throw new IdentityValidationException("identity must not be null");
        if (identity.id() == null) throw new IdentityValidationException("identity id must not be null");
        if (identity.type() == null) throw new IdentityValidationException("identity type must not be null");
        if (identity.status() == null) throw new IdentityValidationException("identity status must not be null");
        if (identity.metadata() == null) throw new IdentityValidationException("identity metadata must not be null");
        if (identity.metadata().name() == null || identity.metadata().name().isBlank())
            throw new IdentityValidationException("identity metadata name must not be blank");
        if (identity.metadata().description() == null)
            throw new IdentityValidationException("identity metadata description must not be null");
    }
}
