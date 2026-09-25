package de.dn.enterprise.platform.publishing.service;

import de.dn.enterprise.platform.publishing.domain.PublicationId;

public final class PublicationNotFoundException extends RuntimeException {

    public PublicationNotFoundException(PublicationId id) {
        super("Publication not found: " + id);
    }
}
