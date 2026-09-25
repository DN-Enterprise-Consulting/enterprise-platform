package de.dn.enterprise.platform.publishing.spi;

import de.dn.enterprise.platform.publishing.domain.Publication;
import de.dn.enterprise.platform.publishing.domain.PublicationId;
import de.dn.enterprise.platform.publishing.domain.PublicationMetadata;
import de.dn.enterprise.platform.publishing.domain.PublicationStatus;
import de.dn.enterprise.platform.publishing.domain.PublicationType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PublicationRepositoryContractTest {

    @Test
    void contractCanBeImplementedWithoutPersistenceTechnology() {
        InMemoryPublicationRepositoryStub repository =
                new InMemoryPublicationRepositoryStub();

        Publication publication = Publication.draft(
                PublicationType.ASSESSMENT_REPORT,
                PublicationMetadata.of("Saved"));

        Publication saved = repository.save(publication);

        assertThat(saved).isNotNull();
        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findByType(PublicationType.ASSESSMENT_REPORT))
                .hasSize(1);
        assertThat(repository.findByStatus(PublicationStatus.DRAFT))
                .hasSize(1);

        // Always use the exact ID returned by save().
        assertThat(repository.findById(saved.id()))
                .contains(saved);
        assertThat(repository.existsById(saved.id()))
                .isTrue();
    }

    @Test
    void repositoryContractExposesExpectedOperations() {
        assertThat(PublicationRepository.class.getDeclaredMethods())
                .extracting(java.lang.reflect.Method::getName)
                .contains(
                        "save",
                        "findById",
                        "findAll",
                        "findByType",
                        "findByStatus",
                        "existsById");
    }

    private static final class InMemoryPublicationRepositoryStub
            implements PublicationRepository {

        private final Map<PublicationId, Publication> publications =
                new LinkedHashMap<>();

        @Override
        public Publication save(Publication publication) {
            publications.put(publication.id(), publication);
            return publication;
        }

        @Override
        public Optional<Publication> findById(PublicationId id) {
            return Optional.ofNullable(publications.get(id));
        }

        @Override
        public List<Publication> findAll() {
            return new ArrayList<>(publications.values());
        }

        @Override
        public List<Publication> findByType(PublicationType type) {
            return publications.values().stream()
                    .filter(publication -> publication.type() == type)
                    .toList();
        }

        @Override
        public List<Publication> findByStatus(PublicationStatus status) {
            return publications.values().stream()
                    .filter(publication -> publication.status() == status)
                    .toList();
        }

        @Override
        public boolean existsById(PublicationId id) {
            return publications.containsKey(id);
        }
    }
}
