package de.dn.enterprise.platform.publishing.spi;

import de.dn.enterprise.platform.publishing.domain.Publication;
import de.dn.enterprise.platform.publishing.domain.PublicationId;

import java.util.Optional;

public interface PublicationPersistence {

    Publication save(Publication publication);

    Optional<Publication> load(PublicationId id);

    boolean exists(PublicationId id);

    void delete(PublicationId id);
}
