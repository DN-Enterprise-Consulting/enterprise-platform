package de.dn.enterprise.platform.assessment.runtime;

import de.dn.enterprise.platform.assessment.api.AssessmentApplication;
import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentRuntimeApiBoundaryTest {

    @Test
    void runtimeExposesStableApplicationBoundary() throws Exception {
        var runtime = AssessmentRuntime.create(
                AssessmentRuntimeConfiguration.fileBacked(Files.createTempDirectory("assessment-runtime-api-")));

        assertThat(runtime.application()).isInstanceOf(AssessmentApplication.class);

        var assessment = runtime.application().create(
                AssessmentType.ENTERPRISE_ARCHITECTURE,
                new AssessmentMetadata("Runtime API", "test", java.util.Map.of()));

        assertThat(runtime.application().get(assessment.id()).id()).isEqualTo(assessment.id());
    }
}
