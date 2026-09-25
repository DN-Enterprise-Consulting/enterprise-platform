package de.dn.enterprise.platform.assessment.service;

import de.dn.enterprise.platform.assessment.domain.AssessmentMetadata;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import de.dn.enterprise.platform.assessment.inmemory.InMemoryAssessmentRepository;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersistentAssessmentServiceTest {

    @Test
    void createsAndPersistsAssessment() {
        var repository = new InMemoryAssessmentRepository();
        var service = new PersistentAssessmentService(repository);

        var assessment = service.create(
                AssessmentType.ENTERPRISE_ARCHITECTURE,
                new AssessmentMetadata("Architecture Assessment", "Initial", Map.of()));

        assertThat(repository.findById(assessment.id())).contains(assessment);
        assertThat(service.get(assessment.id())).isEqualTo(assessment);
    }

    @Test
    void persistsLifecycleTransitions() {
        var repository = new InMemoryAssessmentRepository();
        var service = new PersistentAssessmentService(repository);

        var assessment = service.create(
                AssessmentType.SOFTWARE_ARCHITECTURE,
                new AssessmentMetadata("Software Assessment", "", Map.of()));

        assessment = service.start(assessment.id());
        assertThat(assessment.status()).isEqualTo(AssessmentStatus.IN_PROGRESS);

        assessment = service.complete(assessment.id());
        assertThat(assessment.status()).isEqualTo(AssessmentStatus.COMPLETED);

        assessment = service.archive(assessment.id());
        assertThat(assessment.status()).isEqualTo(AssessmentStatus.ARCHIVED);
        assertThat(service.get(assessment.id()).status()).isEqualTo(AssessmentStatus.ARCHIVED);
    }

    @Test
    void rejectsInvalidCreateBeforePersistence() {
        var repository = new InMemoryAssessmentRepository();
        var service = new PersistentAssessmentService(repository);

        assertThatThrownBy(() -> service.create(
                AssessmentType.ENTERPRISE_ARCHITECTURE,
                new AssessmentMetadata("", "", Map.of())))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void rejectsInvalidLifecycleTransition() {
        var repository = new InMemoryAssessmentRepository();
        var service = new PersistentAssessmentService(repository);

        var assessment = service.create(
                AssessmentType.APPLICATION_PORTFOLIO,
                new AssessmentMetadata("Portfolio", "", Map.of()));

        assertThatThrownBy(() -> service.complete(assessment.id()))
                .isInstanceOf(AssessmentLifecycleException.class);
    }

    @Test
    void throwsForUnknownAssessment() {
        var service = new PersistentAssessmentService(new InMemoryAssessmentRepository());
        var unknownId = de.dn.enterprise.platform.assessment.domain.AssessmentId.newId();

        assertThatThrownBy(() -> service.get(unknownId))
                .isInstanceOf(AssessmentNotFoundException.class);
    }
}
