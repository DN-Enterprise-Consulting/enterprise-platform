package de.dn.enterprise.platform.workflow.runtime;

import de.dn.enterprise.platform.workflow.application.WorkflowApplication;
import de.dn.enterprise.platform.workflow.persistence.FileWorkflowPersistence;
import de.dn.enterprise.platform.workflow.repository.FileWorkflowRepository;
import de.dn.enterprise.platform.workflow.service.PersistentWorkflowService;

import java.nio.file.Path;
import java.util.Objects;

public final class WorkflowRuntime {

    private final PersistentWorkflowService workflowService;
    private final WorkflowApplication application;

    private WorkflowRuntime(
            PersistentWorkflowService workflowService,
            WorkflowApplication application) {
        this.workflowService = workflowService;
        this.application = application;
    }

    public static WorkflowRuntime fileBacked(Path storageDirectory) {
        Objects.requireNonNull(storageDirectory, "storageDirectory must not be null");

        FileWorkflowPersistence persistence = new FileWorkflowPersistence(storageDirectory);
        FileWorkflowRepository repository = new FileWorkflowRepository(persistence);
        PersistentWorkflowService service = new PersistentWorkflowService(repository);

        return new WorkflowRuntime(service, new WorkflowApplication(service));
    }

    public WorkflowApplication application() {
        return application;
    }

    public PersistentWorkflowService workflowService() {
        return workflowService;
    }
}
