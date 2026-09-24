package de.dn.enterprise.platform.sharedkernel.error;

/** Base exception for domain-level invariant violations. */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
