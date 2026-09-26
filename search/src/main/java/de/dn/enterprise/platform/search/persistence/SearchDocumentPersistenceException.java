package de.dn.enterprise.platform.search.persistence;

/** Technical failure while persisting a search document. */
public final class SearchDocumentPersistenceException extends RuntimeException {

    public SearchDocumentPersistenceException(String message) {
        super(message);
    }

    public SearchDocumentPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
