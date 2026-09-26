package de.dn.enterprise.platform.search.query;

import de.dn.enterprise.platform.search.domain.SearchDocumentStatus;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchQueryHardeningTest {

    @Test
    void blankTextIsNormalizedToNoTextQuery() {
        SearchQuery query = new SearchQuery("   ", null, null, Map.of(), 10);

        assertThat(query.text()).isNull();
    }

    @Test
    void zeroLimitIsRejected() {
        assertThatThrownBy(() -> new SearchQuery(null, null, null, Map.of(), 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("limit must be greater than zero");
    }

    @Test
    void negativeLimitIsRejected() {
        assertThatThrownBy(() -> new SearchQuery(null, null, null, Map.of(), -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void attributesAreDefensivelyCopied() {
        var attributes = new java.util.HashMap<String, String>();
        attributes.put("source", "test");
        SearchQuery query = new SearchQuery(null, SearchDocumentType.KNOWLEDGE,
                SearchDocumentStatus.ACTIVE, attributes, 5);

        attributes.put("source", "changed");

        assertThat(query.attributes()).containsEntry("source", "test");
    }

    @Test
    void allQueryUsesMaximumLimit() {
        assertThat(SearchQuery.all().limit()).isEqualTo(Integer.MAX_VALUE);
    }
}
