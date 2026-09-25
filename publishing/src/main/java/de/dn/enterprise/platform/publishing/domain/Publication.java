package de.dn.enterprise.platform.publishing.domain;

import java.util.Objects;

public record Publication(
        PublicationId id,
        PublicationType type,
        PublicationStatus status,
        PublicationMetadata metadata) {

    public Publication {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(metadata, "metadata must not be null");
    }

    public static Publication draft(PublicationType type, PublicationMetadata metadata) {
        return new Publication(PublicationId.newId(), type, PublicationStatus.DRAFT, metadata);
    }

    public Publication withStatus(PublicationStatus newStatus) {
        return new Publication(id, type, newStatus, metadata);
    }

    public Publication withMetadata(PublicationMetadata newMetadata) {
        return new Publication(id, type, status, newMetadata);
    }
}
