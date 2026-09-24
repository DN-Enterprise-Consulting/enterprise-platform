package de.dn.enterprise.platform.sharedkernel.error;

/** Indicates invalid input at a domain/application boundary. */
public final class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }
}
