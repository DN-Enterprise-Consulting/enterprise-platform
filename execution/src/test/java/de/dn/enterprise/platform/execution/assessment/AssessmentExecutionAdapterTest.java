package de.dn.enterprise.platform.execution.assessment;

import de.dn.enterprise.platform.assessment.api.AssessmentApplication;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import de.dn.enterprise.platform.assessment.inmemory.InMemoryAssessmentRepository;
import de.dn.enterprise.platform.assessment.query.AssessmentQueryService;
import de.dn.enterprise.platform.assessment.service.AssessmentLifecycleService;
import de.dn.enterprise.platform.assessment.service.PersistentAssessmentService;
import de.dn.enterprise.platform.assessment.validation.AssessmentValidator;
import de.dn.enterprise.platform.execution.domain.ExecutionContext;
import de.dn.enterprise.platform.execution.domain.ExecutionResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssessmentExecutionAdapterTest {

    @Test
    void executesAssessmentThroughApplicationBoundary() {
        InMemoryAssessmentRepository repository = new InMemoryAssessmentRepository();
        AssessmentValidator validator = new AssessmentValidator();
        PersistentAssessmentService service =
                new PersistentAssessmentService(
                        repository,
                        new AssessmentLifecycleService(),
                        validator);
        AssessmentApplication application =
                new AssessmentApplication(service, new AssessmentQueryService(repository));

        AssessmentExecutionAdapter adapter = new AssessmentExecutionAdapter(application);

        ExecutionResult result = adapter.execute(ExecutionContext.empty()
                .with(AssessmentExecutionAdapter.TYPE_KEY,
                        AssessmentType.ENTERPRISE_ARCHITECTURE.name())
                .with(AssessmentExecutionAdapter.NAME_KEY, "Execution Assessment")
                .with(AssessmentExecutionAdapter.DESCRIPTION_KEY, "Created by execution adapter"));

        assertThat(result.successful()).isTrue();
        assertThat(result.outputs()).containsKeys("assessmentId", "assessmentType", "assessmentStatus");
        assertThat(result.outputs()).containsEntry(
                "assessmentType", AssessmentType.ENTERPRISE_ARCHITECTURE.name());
        assertThat(result.outputs()).containsEntry(
                "assessmentStatus", AssessmentStatus.COMPLETED.name());

        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findAll().get(0).status())
                .isEqualTo(AssessmentStatus.COMPLETED);
    }

    @Test
    void rejectsMissingAssessmentType() {
        AssessmentExecutionAdapter adapter = adapter();

        assertThatThrownBy(() -> adapter.execute(ExecutionContext.empty()
                .with(AssessmentExecutionAdapter.NAME_KEY, "Assessment")
                .with(AssessmentExecutionAdapter.DESCRIPTION_KEY, "Description")))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void exposesStableDomainKey() {
        assertThat(adapter().domainKey()).isEqualTo("assessment");
    }

    private AssessmentExecutionAdapter adapter() {
        InMemoryAssessmentRepository repository = new InMemoryAssessmentRepository();
        AssessmentValidator validator = new AssessmentValidator();
        PersistentAssessmentService service =
                new PersistentAssessmentService(
                        repository,
                        new AssessmentLifecycleService(),
                        validator);
        AssessmentApplication application =
                new AssessmentApplication(service, new AssessmentQueryService(repository));
        return new AssessmentExecutionAdapter(application);
    }
}
