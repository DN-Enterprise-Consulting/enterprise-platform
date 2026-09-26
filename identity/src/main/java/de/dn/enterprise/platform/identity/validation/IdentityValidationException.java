package de.dn.enterprise.platform.identity.validation;

/** Raised when an identity violates the identity boundary invariants. */
public final class IdentityValidationException extends IllegalArgumentException {
    public IdentityValidationException(String message) { super(message); }
}
