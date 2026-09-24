package de.dn.enterprise.platform.knowledge.service;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectMetadata;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectStatus;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KnowledgeLifecycleServiceTest {

    private final KnowledgeLifecycleService service = new KnowledgeLifecycleService();

    @Test
    void allowsDraftToActive() {
        var object = object(KnowledgeObjectStatus.DRAFT);

        assertThat(service.activate(object).status())
                .isEqualTo(KnowledgeObjectStatus.ACTIVE);
    }

    @Test
    void allowsActiveToDeprecated() {
        var object = object(KnowledgeObjectStatus.ACTIVE);

        assertThat(service.deprecate(object).status())
                .isEqualTo(KnowledgeObjectStatus.DEPRECATED);
    }

    @Test
    void allowsDeprecatedToArchived() {
        var object = object(KnowledgeObjectStatus.DEPRECATED);

        assertThat(service.archive(object).status())
                .isEqualTo(KnowledgeObjectStatus.ARCHIVED);
    }

    @Test
    void rejectsActivationFromActive() {
        assertThatThrownBy(() -> service.activate(object(KnowledgeObjectStatus.ACTIVE)))
                .isInstanceOf(KnowledgeLifecycleException.class);
    }

    @Test
    void rejectsActivationFromDeprecated() {
        assertThatThrownBy(() -> service.activate(object(KnowledgeObjectStatus.DEPRECATED)))
                .isInstanceOf(KnowledgeLifecycleException.class);
    }

    @Test
    void rejectsDeprecationFromDraft() {
        assertThatThrownBy(() -> service.deprecate(object(KnowledgeObjectStatus.DRAFT)))
                .isInstanceOf(KnowledgeLifecycleException.class);
    }

    @Test
    void rejectsArchivingFromActive() {
        assertThatThrownBy(() -> service.archive(object(KnowledgeObjectStatus.ACTIVE)))
                .isInstanceOf(KnowledgeLifecycleException.class);
    }

    @Test
    void rejectsAnyTransitionFromArchived() {
        var archived = object(KnowledgeObjectStatus.ARCHIVED);

        assertThatThrownBy(() -> service.activate(archived))
                .isInstanceOf(KnowledgeLifecycleException.class);
        assertThatThrownBy(() -> service.deprecate(archived))
                .isInstanceOf(KnowledgeLifecycleException.class);
        assertThatThrownBy(() -> service.archive(archived))
                .isInstanceOf(KnowledgeLifecycleException.class);
    }

    private static de.dn.enterprise.platform.knowledge.domain.KnowledgeObject object(
            KnowledgeObjectStatus status) {
        return new de.dn.enterprise.platform.knowledge.domain.KnowledgeObject(
                de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId.newId(),
                KnowledgeObjectType.STANDARD,
                de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectVersion.initial(),
                status,
                new KnowledgeObjectMetadata("Lifecycle Test", "", Map.of()),
                "content");
    }
}
