package de.dn.enterprise.platform.knowledge.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeObjectVersionTest {

    @Test
    void supportsSemanticVersionSteps() {
        var version = new KnowledgeObjectVersion(1, 2, 3);

        assertThat(version.nextMajor()).isEqualTo(new KnowledgeObjectVersion(2, 0, 0));
        assertThat(version.nextMinor()).isEqualTo(new KnowledgeObjectVersion(1, 3, 0));
        assertThat(version.nextPatch()).isEqualTo(new KnowledgeObjectVersion(1, 2, 4));
    }

    @Test
    void rejectsNegativeComponents() {
        assertThatThrownBy(() -> new KnowledgeObjectVersion(-1, 0, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
