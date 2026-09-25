package de.dn.enterprise.platform.assessment.runtime;

import java.nio.file.Path;
import java.util.Objects;

public record AssessmentRuntimeConfiguration(Path storageDirectory) {

    public AssessmentRuntimeConfiguration {
        Objects.requireNonNull(storageDirectory, "storageDirectory must not be null");
    }

    public static AssessmentRuntimeConfiguration fileBacked(Path storageDirectory) {
        return new AssessmentRuntimeConfiguration(storageDirectory);
    }
}
