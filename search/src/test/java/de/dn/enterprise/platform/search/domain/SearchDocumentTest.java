package de.dn.enterprise.platform.search.domain;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchDocumentTest {

    @Test
    void createsDocumentWithGeneratedIdAndActiveStatus() {
        SearchDocument document = SearchDocument.create(
                SearchDocumentType.KNOWLEDGE,
                SearchDocumentMetadata.of("Architecture", "Enterprise architecture knowledge"),
                "content",
                Map.of("domain", "architecture"));

        assertThat(document.id()).isNotNull();
        assertThat(document.type()).isEqualTo(SearchDocumentType.KNOWLEDGE);
        assertThat(document.status()).isEqualTo(SearchDocumentStatus.ACTIVE);
        assertThat(document.content()).isEqualTo("content");
        assertThat(document.attributes()).containsEntry("domain", "architecture");
    }

    @Test
    void copiesAttributesImmutably() {
        Map<String, String> attributes = new java.util.HashMap<>();
        attributes.put("key", "value");

        SearchDocument document = SearchDocument.create(
                SearchDocumentType.RULE,
                SearchDocumentMetadata.of("Rule", "Rule description"),
                "definition",
                attributes);

        attributes.put("other", "changed");

        assertThat(document.attributes()).containsOnlyKeys("key");
        assertThatThrownBy(() -> document.attributes().put("x", "y"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void rejectsInvalidMetadata() {
        assertThatThrownBy(() -> SearchDocumentMetadata.of(" ", "description"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
