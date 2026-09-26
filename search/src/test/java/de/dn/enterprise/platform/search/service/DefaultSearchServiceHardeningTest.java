package de.dn.enterprise.platform.search.service;

import de.dn.enterprise.platform.search.domain.SearchDocumentId;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;
import de.dn.enterprise.platform.search.inmemory.InMemorySearchDocumentRepository;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultSearchServiceHardeningTest {

    @Test
    void nullRepositoryIsRejected() {
        assertThatThrownBy(() -> new DefaultSearchService(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("repository must not be null");
    }

    @Test
    void nullCreateTypeIsRejected() {
        var service = new DefaultSearchService(new InMemorySearchDocumentRepository());

        assertThatThrownBy(() -> service.create(null, "Title", "Description", "Content", Map.of()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("type must not be null");
    }

    @Test
    void updateOfUnknownDocumentIsRejected() {
        var service = new DefaultSearchService(new InMemorySearchDocumentRepository());
        var document = service.create(SearchDocumentType.KNOWLEDGE, "Title", "Description", "Content", Map.of());
        service.delete(document.id());

        assertThatThrownBy(() -> service.update(document))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullIdentifiersAreRejected() {
        var service = new DefaultSearchService(new InMemorySearchDocumentRepository());

        assertThatThrownBy(() -> service.get(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> service.delete(null))
                .isInstanceOf(NullPointerException.class);
    }
}
