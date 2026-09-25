package de.dn.enterprise.platform.publishing.service;

import de.dn.enterprise.platform.publishing.domain.PublicationStatus;

public final class PublicationLifecycleException extends RuntimeException {

    public PublicationLifecycleException(
            PublicationStatus current,
            PublicationStatus target) {
        super("Invalid publication lifecycle transition: "
                + current + " -> " + target);
    }
}
