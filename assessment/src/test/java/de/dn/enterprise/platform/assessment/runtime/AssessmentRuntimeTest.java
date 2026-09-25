package de.dn.enterprise.platform.assessment.runtime;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentRuntimeTest {

    @Test
    void createsFileBackedRuntime() throws Exception {
        var storage = Files.createTempDirectory("assessment-runtime-");

        AssessmentRuntime runtime = AssessmentRuntime.create(
                AssessmentRuntimeConfiguration.fileBacked(storage));

        Assessment assessment = runtime.repository().save(
                Assessment.draft(
                        AssessmentType.ENTERPRISE_ARCHITECTURE,
                        new AssessmentMetadata(
                                "Architecture Assessment",
                                "Test",
                                Map.of())));

        assertThat(runtime.repository().findById(assessment.id())).contains(assessment);
        assertThat(runtime.queryService().findAll()).containsExactly(assessment);
        assertThat(runtime.validator()).isNotNull();
    }

    @Test
    void factoryCreatesRuntime() throws Exception {
        var storage = Files.createTempDirectory("assessment-runtime-factory-");

        AssessmentRuntime runtime = AssessmentRuntimeFactory.create(
                new AssessmentRuntimeConfiguration(storage));

        assertThat(runtime).isNotNull();
        assertThat(runtime.repository()).isNotNull();
        assertThat(runtime.queryService()).isNotNull();
        assertThat(runtime.validator()).isNotNull();
    }
}
