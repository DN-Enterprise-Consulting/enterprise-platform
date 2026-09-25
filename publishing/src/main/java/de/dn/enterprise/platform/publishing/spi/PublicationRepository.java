package de.dn.enterprise.platform.publishing.spi;

import de.dn.enterprise.platform.publishing.domain.Publication;
import de.dn.enterprise.platform.publishing.domain.PublicationId;
import de.dn.enterprise.platform.publishing.domain.PublicationStatus;
import de.dn.enterprise.platform.publishing.domain.PublicationType;

import java.util.List;
import java.util.Optional;

public interface PublicationRepository {

    Publication save(Publication publication);

    Optional<Publication> findById(PublicationId id);

    List<Publication> findAll();

    List<Publication> findByType(PublicationType type);

    List<Publication> findByStatus(PublicationStatus status);

    boolean existsById(PublicationId id);
}
