package de.dn.enterprise.platform.assessment.runtime;

import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentRuntimeIntegrationTest {

    @Test
    void persistsAssessmentAcrossRuntimeRecreation() throws Exception {
        var storage = Files.createTempDirectory("assessment-m210-");
        var configuration = AssessmentRuntimeConfiguration.fileBacked(storage);

        var firstRuntime = AssessmentRuntimeFactory.create(configuration);
        var created = firstRuntime.service().create(
                AssessmentType.ENTERPRISE_ARCHITECTURE,
                new AssessmentMetadata("Persistent Assessment", "Integration", Map.of()));
        firstRuntime.service().start(created.id());
        firstRuntime.service().complete(created.id());

        var secondRuntime = AssessmentRuntimeFactory.create(configuration);
        var loaded = secondRuntime.service().get(created.id());

        assertThat(loaded.status()).isEqualTo(AssessmentStatus.COMPLETED);
        assertThat(secondRuntime.queryService().findAll()).containsExactly(loaded);
    }
}
