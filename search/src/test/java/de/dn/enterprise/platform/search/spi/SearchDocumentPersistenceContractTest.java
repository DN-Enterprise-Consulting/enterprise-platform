package de.dn.enterprise.platform.search.spi;

import de.dn.enterprise.platform.search.domain.SearchDocument;
import de.dn.enterprise.platform.search.domain.SearchDocumentId;
import de.dn.enterprise.platform.search.domain.SearchDocumentMetadata;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SearchDocumentPersistenceContractTest {

    @Test
    void exposesPersistenceOperations() throws Exception {
        assertThat(SearchDocumentPersistence.class.getMethod("save", SearchDocument.class)).isNotNull();
        assertThat(SearchDocumentPersistence.class.getMethod("load", SearchDocumentId.class)).isNotNull();
        assertThat(SearchDocumentPersistence.class.getMethod("loadAll")).isNotNull();
        assertThat(SearchDocumentPersistence.class.getMethod("exists", SearchDocumentId.class)).isNotNull();
        assertThat(SearchDocumentPersistence.class.getMethod("delete", SearchDocumentId.class)).isNotNull();
    }

    @Test
    void domainTypesRemainUsableForContractConsumers() {
        SearchDocument document = SearchDocument.create(
                SearchDocumentType.KNOWLEDGE,
                SearchDocumentMetadata.of("Test", "Description"),
                "content",
                Map.of("source", "contract-test")
        );

        assertThat(document.id()).isNotNull();
        assertThat(document.type()).isEqualTo(SearchDocumentType.KNOWLEDGE);
        assertThat(document.content()).isEqualTo("content");
    }
}
