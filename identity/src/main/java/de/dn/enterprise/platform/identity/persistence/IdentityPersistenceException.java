package de.dn.enterprise.platform.identity.persistence;

/** Technical failure while persisting an identity. */
public final class IdentityPersistenceException extends RuntimeException {

    public IdentityPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdentityPersistenceException(String message) {
        super(message);
    }
}
