package de.dn.enterprise.platform.publishing.spi;

import de.dn.enterprise.platform.publishing.domain.Publication;
import de.dn.enterprise.platform.publishing.domain.PublicationId;
import de.dn.enterprise.platform.publishing.domain.PublicationMetadata;
import de.dn.enterprise.platform.publishing.domain.PublicationType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PublicationPersistenceContractTest {

    @Test
    void contractCanBeImplementedWithoutPersistenceTechnology() {
        InMemoryPersistenceStub persistence = new InMemoryPersistenceStub();

        Publication publication = Publication.draft(
                PublicationType.ASSESSMENT_REPORT,
                PublicationMetadata.of("Test"));

        Publication saved = persistence.save(publication);

        assertThat(saved).isSameAs(publication);
        assertThat(persistence.load(publication.id())).containsSame(publication);
        assertThat(persistence.exists(publication.id())).isTrue();

        persistence.delete(publication.id());

        assertThat(persistence.load(publication.id())).isEmpty();
        assertThat(persistence.exists(publication.id())).isFalse();
    }

    private static final class InMemoryPersistenceStub
            implements PublicationPersistence {

        private final Map<PublicationId, Publication> publications = new HashMap<>();

        @Override
        public Publication save(Publication publication) {
            publications.put(publication.id(), publication);
            return publication;
        }

        @Override
        public Optional<Publication> load(PublicationId id) {
            return Optional.ofNullable(publications.get(id));
        }

        @Override
        public boolean exists(PublicationId id) {
            return publications.containsKey(id);
        }

        @Override
        public void delete(PublicationId id) {
            publications.remove(id);
        }
    }
}
