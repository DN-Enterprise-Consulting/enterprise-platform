package de.dn.enterprise.platform.assessment.api;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import de.dn.enterprise.platform.assessment.persistence.FileAssessmentPersistence;
import de.dn.enterprise.platform.assessment.persistence.FileAssessmentRepository;
import de.dn.enterprise.platform.assessment.query.AssessmentQueryService;
import de.dn.enterprise.platform.assessment.service.AssessmentLifecycleService;
import de.dn.enterprise.platform.assessment.service.PersistentAssessmentService;
import de.dn.enterprise.platform.assessment.validation.AssessmentValidator;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentApplicationTest {

    @Test
    void exposesUseCasesWithoutRequiringInfrastructureTypes() throws Exception {
        var directory = Files.createTempDirectory("assessment-api-");
        var persistence = new FileAssessmentPersistence(directory);
        var repository = new FileAssessmentRepository(persistence);
        var queryService = new AssessmentQueryService(repository);
        var service = new PersistentAssessmentService(
                repository,
                new AssessmentLifecycleService(),
                new AssessmentValidator());
        var application = new AssessmentApplication(service, queryService);

        Assessment created = application.create(
                AssessmentType.SOFTWARE_ARCHITECTURE,
                new AssessmentMetadata("API boundary", "test", java.util.Map.of()));

        application.start(created.id());
        Assessment completed = application.complete(created.id());

        assertThat(completed.status()).isEqualTo(AssessmentStatus.COMPLETED);
        assertThat(application.get(created.id()).status()).isEqualTo(AssessmentStatus.COMPLETED);
        assertThat(application.findByStatus(AssessmentStatus.COMPLETED))
                .extracting(Assessment::id)
                .containsExactly(created.id());
    }
}
