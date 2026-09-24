package de.dn.enterprise.platform.knowledge.domain;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KnowledgeObjectMetadataTest {

    @Test
    void copiesAttributesToKeepMetadataImmutable() {
        Map<String, String> source = new HashMap<>();
        source.put("owner", "DN");

        var metadata = new KnowledgeObjectMetadata("Name", null, source);
        source.put("owner", "changed");

        assertThat(metadata.attributes()).containsEntry("owner", "DN");
    }

    @Test
    void rejectsBlankName() {
        assertThatThrownBy(() -> new KnowledgeObjectMetadata(" ", null, Map.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
