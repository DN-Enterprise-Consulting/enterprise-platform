package de.dn.enterprise.platform.execution.publishing;

import de.dn.enterprise.platform.execution.domain.ExecutionContext;
import de.dn.enterprise.platform.execution.domain.ExecutionResult;
import de.dn.enterprise.platform.execution.spi.ExecutionAdapter;
import de.dn.enterprise.platform.publishing.application.PublicationApplication;
import de.dn.enterprise.platform.publishing.domain.Publication;
import de.dn.enterprise.platform.publishing.domain.PublicationMetadata;
import de.dn.enterprise.platform.publishing.domain.PublicationType;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Execution adapter for the Publishing domain.
 *
 * Creates a publication through the stable application boundary and advances
 * it from DRAFT to PUBLISHED. Rendering/content generation is intentionally
 * outside this adapter because the current Publishing domain exposes no
 * renderer or document-content contract.
 */
public final class PublishingExecutionAdapter implements ExecutionAdapter {

    public static final String DOMAIN_KEY = "publishing";
    public static final String TYPE_KEY = "publication.type";
    public static final String NAME_KEY = "publication.name";
    public static final String DESCRIPTION_KEY = "publication.description";

    private final PublicationApplication application;

    public PublishingExecutionAdapter(PublicationApplication application) {
        this.application = Objects.requireNonNull(
                application, "application must not be null");
    }

    @Override
    public String domainKey() {
        return DOMAIN_KEY;
    }

    @Override
    public ExecutionResult execute(ExecutionContext context) {
        Objects.requireNonNull(context, "context must not be null");

        PublicationType type = required(context, TYPE_KEY)
                .map(PublicationType::valueOf)
                .orElseThrow();
        String name = required(context, NAME_KEY).orElseThrow();

        Publication publication = application.create(
                type,
                PublicationMetadata.of(name));

        publication = application.publish(publication.id());

        return ExecutionResult.success(
                "publishing execution completed",
                Map.of(
                        "publicationId", publication.id().value().toString(),
                        "publicationType", publication.type().name(),
                        "publicationStatus", publication.status().name(),
                        "publicationName", publication.metadata().name()));
    }

    private Optional<String> required(ExecutionContext context, String key) {
        return context.get(key).filter(value -> !value.isBlank());
    }
}
