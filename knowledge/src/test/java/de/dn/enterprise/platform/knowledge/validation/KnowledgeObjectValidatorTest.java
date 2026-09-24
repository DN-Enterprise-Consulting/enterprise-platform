package de.dn.enterprise.platform.knowledge.validation;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectMetadata;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectStatus;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectVersion;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KnowledgeObjectValidatorTest {

    private final KnowledgeObjectValidator validator = new KnowledgeObjectValidator();

    @Test
    void acceptsValidKnowledgeObject() {
        assertThatCode(() -> validator.validate(object("Standard", "valid content")))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsNullObject() {
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsBlankNameAtDomainBoundary() {
        assertThatThrownBy(() -> new KnowledgeObjectMetadata(
                " ",
                "",
                Map.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name must not be blank");
    }

    @Test
    void rejectsBlankContent() {
        assertThatThrownBy(() -> validator.validate(object("Standard", " ")))
                .isInstanceOf(KnowledgeValidationException.class)
                .hasMessage("content must not be blank");
    }

    private static KnowledgeObject object(String name, String content) {
        return new KnowledgeObject(
                KnowledgeObjectId.newId(),
                KnowledgeObjectType.STANDARD,
                KnowledgeObjectVersion.initial(),
                KnowledgeObjectStatus.DRAFT,
                new KnowledgeObjectMetadata(name, "", Map.of()),
                content);
    }
}
