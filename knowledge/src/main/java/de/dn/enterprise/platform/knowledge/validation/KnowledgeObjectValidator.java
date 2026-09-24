package de.dn.enterprise.platform.knowledge.validation;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectMetadata;

import java.util.Objects;

public final class KnowledgeObjectValidator {

    public void validate(KnowledgeObject object) {
        Objects.requireNonNull(object, "knowledgeObject must not be null");

        validateMetadata(object.metadata());
        validateContent(object.content());
    }

    private void validateMetadata(KnowledgeObjectMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata must not be null");

        if (metadata.name().isBlank()) {
            throw new KnowledgeValidationException("metadata.name must not be blank");
        }
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new KnowledgeValidationException("content must not be blank");
        }
    }
}
