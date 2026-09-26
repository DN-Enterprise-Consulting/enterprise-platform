package de.dn.enterprise.platform.execution.publishing;

import de.dn.enterprise.platform.execution.domain.ExecutionContext;
import de.dn.enterprise.platform.execution.domain.ExecutionResult;
import de.dn.enterprise.platform.publishing.application.PublicationApplication;
import de.dn.enterprise.platform.publishing.domain.PublicationStatus;
import de.dn.enterprise.platform.publishing.domain.PublicationType;
import de.dn.enterprise.platform.publishing.inmemory.InMemoryPublicationRepository;
import de.dn.enterprise.platform.publishing.service.PersistentPublicationService;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PublishingExecutionAdapterTest {

    @Test
    void executesPublicationCreationAndPublishing() {
        PublicationApplication application = new PublicationApplication(
                new PersistentPublicationService(
                        new InMemoryPublicationRepository()));
        PublishingExecutionAdapter adapter =
                new PublishingExecutionAdapter(application);

        ExecutionResult result = adapter.execute(new ExecutionContext(Map.of(
                PublishingExecutionAdapter.TYPE_KEY,
                PublicationType.ASSESSMENT_REPORT.name(),
                PublishingExecutionAdapter.NAME_KEY,
                "Assessment Report",
                PublishingExecutionAdapter.DESCRIPTION_KEY,
                "Enterprise assessment publication")));

        assertThat(result.successful()).isTrue();
        assertThat(result.outputs()).containsEntry(
                "publicationType", PublicationType.ASSESSMENT_REPORT.name());
        assertThat(result.outputs()).containsEntry(
                "publicationStatus", PublicationStatus.PUBLISHED.name());
        assertThat(result.outputs()).containsEntry(
                "publicationName", "Assessment Report");
        assertThat(application.findByStatus(PublicationStatus.PUBLISHED))
                .hasSize(1);
    }

    @Test
    void exposesPublishingDomainKey() {
        PublicationApplication application = new PublicationApplication(
                new PersistentPublicationService(
                        new InMemoryPublicationRepository()));
        PublishingExecutionAdapter adapter =
                new PublishingExecutionAdapter(application);

        assertThat(adapter.domainKey()).isEqualTo("publishing");
    }

    @Test
    void rejectsMissingRequiredContext() {
        PublicationApplication application = new PublicationApplication(
                new PersistentPublicationService(
                        new InMemoryPublicationRepository()));
        PublishingExecutionAdapter adapter =
                new PublishingExecutionAdapter(application);

        assertThatThrownBy(() -> adapter.execute(new ExecutionContext(Map.of(
                PublishingExecutionAdapter.TYPE_KEY,
                PublicationType.TECHNICAL_REPORT.name()))))
                .isInstanceOf(RuntimeException.class);
    }
}
