package de.dn.enterprise.platform.knowledge.domain;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeObjectTest {

    @Test
    void createsDraftWithGeneratedIdentityAndInitialVersion() {
        var object = KnowledgeObject.draft(
                KnowledgeObjectType.STANDARD,
                new KnowledgeObjectMetadata("Test Standard", "Description", Map.of("owner", "DN")),
                "content");

        assertThat(object.id()).isNotNull();
        assertThat(object.version()).isEqualTo(KnowledgeObjectVersion.initial());
        assertThat(object.status()).isEqualTo(KnowledgeObjectStatus.DRAFT);
        assertThat(object.metadata().attributes()).containsEntry("owner", "DN");
    }

    @Test
    void statusChangeCreatesNewInstance() {
        var object = KnowledgeObject.draft(
                KnowledgeObjectType.RULE,
                new KnowledgeObjectMetadata("Rule", null, null),
                "content");

        var active = object.withStatus(KnowledgeObjectStatus.ACTIVE);

        assertThat(object.status()).isEqualTo(KnowledgeObjectStatus.DRAFT);
        assertThat(active.status()).isEqualTo(KnowledgeObjectStatus.ACTIVE);
        assertThat(active.id()).isEqualTo(object.id());
    }
}
