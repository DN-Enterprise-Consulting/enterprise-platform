package de.dn.enterprise.platform.execution.assessment;

import de.dn.enterprise.platform.assessment.api.AssessmentApplication;
import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import de.dn.enterprise.platform.execution.domain.ExecutionContext;
import de.dn.enterprise.platform.execution.domain.ExecutionResult;
import de.dn.enterprise.platform.execution.spi.ExecutionAdapter;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class AssessmentExecutionAdapter implements ExecutionAdapter {

    public static final String DOMAIN_KEY = "assessment";
    public static final String TYPE_KEY = "assessment.type";
    public static final String NAME_KEY = "assessment.name";
    public static final String DESCRIPTION_KEY = "assessment.description";

    private final AssessmentApplication application;

    public AssessmentExecutionAdapter(AssessmentApplication application) {
        this.application = Objects.requireNonNull(application, "application must not be null");
    }

    @Override
    public String domainKey() {
        return DOMAIN_KEY;
    }

    @Override
    public ExecutionResult execute(ExecutionContext context) {
        Objects.requireNonNull(context, "context must not be null");

        AssessmentType type = required(context, TYPE_KEY)
                .map(AssessmentType::valueOf)
                .orElseThrow();

        String name = required(context, NAME_KEY).orElseThrow();
        String description = required(context, DESCRIPTION_KEY).orElseThrow();

        Assessment assessment = application.create(
                type,
                new AssessmentMetadata(name, description, Map.of()));

        assessment = application.start(assessment.id());
        assessment = application.complete(assessment.id());

        return ExecutionResult.success(
                "assessment execution completed",
                Map.of(
                        "assessmentId", assessment.id().value().toString(),
                        "assessmentType", assessment.type().name(),
                        "assessmentStatus", assessment.status().name()));
    }

    private Optional<String> required(ExecutionContext context, String key) {
        return context.get(key).filter(value -> !value.isBlank());
    }
}
