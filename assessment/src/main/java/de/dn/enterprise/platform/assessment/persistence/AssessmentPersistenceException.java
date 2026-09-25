package de.dn.enterprise.platform.assessment.persistence;

public final class AssessmentPersistenceException extends RuntimeException {

    public AssessmentPersistenceException(String message) {
        super(message);
    }

    public AssessmentPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
