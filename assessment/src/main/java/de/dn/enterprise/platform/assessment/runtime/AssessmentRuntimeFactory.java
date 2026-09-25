package de.dn.enterprise.platform.assessment.runtime;

import java.util.Objects;

public final class AssessmentRuntimeFactory {

    private AssessmentRuntimeFactory() {
    }

    public static AssessmentRuntime create(AssessmentRuntimeConfiguration configuration) {
        Objects.requireNonNull(configuration, "configuration must not be null");
        return AssessmentRuntime.create(configuration);
    }
}
