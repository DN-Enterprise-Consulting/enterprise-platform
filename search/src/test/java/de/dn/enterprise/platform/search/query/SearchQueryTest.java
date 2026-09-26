package de.dn.enterprise.platform.search.query;

import de.dn.enterprise.platform.search.domain.SearchDocumentStatus;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchQueryTest {
    @Test
    void allQueryHasNoFilters() {
        SearchQuery query = SearchQuery.all();
        assertThat(query.text()).isNull();
        assertThat(query.type()).isNull();
        assertThat(query.status()).isNull();
        assertThat(query.attributes()).isEmpty();
    }

    @Test
    void builderMethodsKeepPreviousValues() {
        SearchQuery query = SearchQuery.text("Java")
                .withType(SearchDocumentType.KNOWLEDGE)
                .withStatus(SearchDocumentStatus.ACTIVE)
                .withAttributes(Map.of("source", "test"))
                .withLimit(5);
        assertThat(query.text()).isEqualTo("Java");
        assertThat(query.type()).isEqualTo(SearchDocumentType.KNOWLEDGE);
        assertThat(query.status()).isEqualTo(SearchDocumentStatus.ACTIVE);
        assertThat(query.attributes()).containsEntry("source", "test");
        assertThat(query.limit()).isEqualTo(5);
    }

    @Test
    void limitMustBePositive() {
        assertThatThrownBy(() -> SearchQuery.all().withLimit(0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
