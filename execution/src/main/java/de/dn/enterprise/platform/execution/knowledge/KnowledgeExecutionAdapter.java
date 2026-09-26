package de.dn.enterprise.platform.execution.knowledge;

import de.dn.enterprise.platform.execution.domain.ExecutionContext;
import de.dn.enterprise.platform.execution.domain.ExecutionResult;
import de.dn.enterprise.platform.execution.spi.ExecutionAdapter;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectMetadata;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import de.dn.enterprise.platform.knowledge.service.PersistentKnowledgeService;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class KnowledgeExecutionAdapter implements ExecutionAdapter {

    public static final String DOMAIN_KEY = "knowledge";
    public static final String TYPE_KEY = "knowledge.type";
    public static final String NAME_KEY = "knowledge.name";
    public static final String DESCRIPTION_KEY = "knowledge.description";
    public static final String CONTENT_KEY = "knowledge.content";

    private final PersistentKnowledgeService service;

    public KnowledgeExecutionAdapter(PersistentKnowledgeService service) {
        this.service = Objects.requireNonNull(service, "service must not be null");
    }

    @Override
    public String domainKey() {
        return DOMAIN_KEY;
    }

    @Override
    public ExecutionResult execute(ExecutionContext context) {
        Objects.requireNonNull(context, "context must not be null");

        KnowledgeObjectType type = required(context, TYPE_KEY)
                .map(KnowledgeObjectType::valueOf)
                .orElseThrow();
        String name = required(context, NAME_KEY).orElseThrow();
        String description = required(context, DESCRIPTION_KEY).orElse("");
        String content = required(context, CONTENT_KEY).orElseThrow();

        KnowledgeObject object = service.create(
                type,
                name,
                description,
                content,
                Map.of());

        object = service.activate(object.id());

        return ExecutionResult.success(
                "knowledge execution completed",
                Map.of(
                        "knowledgeId", object.id().value().toString(),
                        "knowledgeType", object.type().name(),
                        "knowledgeStatus", object.status().name(),
                        "knowledgeVersion", object.version().toString()));
    }

    private Optional<String> required(ExecutionContext context, String key) {
        return context.get(key).filter(value -> !value.isBlank());
    }
}
