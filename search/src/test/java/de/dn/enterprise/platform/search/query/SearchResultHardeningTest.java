package de.dn.enterprise.platform.search.query;

import de.dn.enterprise.platform.search.domain.SearchDocument;
import de.dn.enterprise.platform.search.domain.SearchDocumentMetadata;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchResultHardeningTest {

    @Test
    void nullDocumentIsRejected() {
        assertThatThrownBy(() -> new SearchResult(null, 1))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("document must not be null");
    }

    @Test
    void negativeScoreIsRejected() {
        SearchDocument document = SearchDocument.create(
                SearchDocumentType.KNOWLEDGE,
                SearchDocumentMetadata.of("Name", "Description"),
                "content", Map.of());

        assertThatThrownBy(() -> new SearchResult(document, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("score must not be negative");
    }
}
