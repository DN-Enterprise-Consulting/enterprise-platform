package de.dn.enterprise.platform.rules.persistence;

public final class RulePersistenceException extends RuntimeException {

    public RulePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public RulePersistenceException(String message) {
        super(message);
    }
}
