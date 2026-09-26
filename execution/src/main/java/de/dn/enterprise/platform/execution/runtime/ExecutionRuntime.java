package de.dn.enterprise.platform.execution.runtime;

import de.dn.enterprise.platform.assessment.api.AssessmentApplication;
import de.dn.enterprise.platform.assessment.runtime.AssessmentRuntime;
import de.dn.enterprise.platform.assessment.runtime.AssessmentRuntimeConfiguration;
import de.dn.enterprise.platform.execution.application.ExecutionApplication;
import de.dn.enterprise.platform.execution.assessment.AssessmentExecutionAdapter;
import de.dn.enterprise.platform.execution.dispatcher.ExecutionStepDispatcher;
import de.dn.enterprise.platform.execution.knowledge.KnowledgeExecutionAdapter;
import de.dn.enterprise.platform.execution.publishing.PublishingExecutionAdapter;
import de.dn.enterprise.platform.execution.registry.InMemoryExecutionAdapterRegistry;
import de.dn.enterprise.platform.execution.rules.RuleEvaluationAdapter;
import de.dn.enterprise.platform.execution.spi.ExecutionAdapterRegistry;
import de.dn.enterprise.platform.knowledge.service.PersistentKnowledgeService;
import de.dn.enterprise.platform.rules.persistence.FileRulePersistence;
import de.dn.enterprise.platform.rules.persistence.FileRuleRepository;
import de.dn.enterprise.platform.rules.service.PersistentRuleService;
import de.dn.enterprise.platform.publishing.application.PublicationApplication;
import de.dn.enterprise.platform.publishing.runtime.PublicationRuntime;
import de.dn.enterprise.platform.bootstrap.KnowledgeRuntime;
import de.dn.enterprise.platform.bootstrap.KnowledgeRuntimeConfiguration;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Composition root for the executable cross-domain runtime.
 *
 * <p>The runtime creates the persistent domain runtimes, wires their stable
 * application/service boundaries into execution adapters, registers the
 * adapters and exposes the step dispatcher as the execution entry point.</p>
 */
public final class ExecutionRuntime {

    private final Path storageDirectory;
    private final AssessmentRuntime assessmentRuntime;
    private final KnowledgeRuntime knowledgeRuntime;
    private final PersistentRuleService ruleService;
    private final PublicationRuntime publicationRuntime;
    private final InMemoryExecutionAdapterRegistry registry;
    private final ExecutionStepDispatcher dispatcher;
    private final ExecutionApplication application;

    private ExecutionRuntime(
            Path storageDirectory,
            AssessmentRuntime assessmentRuntime,
            KnowledgeRuntime knowledgeRuntime,
            PersistentRuleService ruleService,
            PublicationRuntime publicationRuntime,
            InMemoryExecutionAdapterRegistry registry,
            ExecutionStepDispatcher dispatcher) {
        this.storageDirectory = Objects.requireNonNull(storageDirectory, "storageDirectory must not be null");
        this.assessmentRuntime = Objects.requireNonNull(assessmentRuntime, "assessmentRuntime must not be null");
        this.knowledgeRuntime = Objects.requireNonNull(knowledgeRuntime, "knowledgeRuntime must not be null");
        this.ruleService = Objects.requireNonNull(ruleService, "ruleService must not be null");
        this.publicationRuntime = Objects.requireNonNull(publicationRuntime, "publicationRuntime must not be null");
        this.registry = Objects.requireNonNull(registry, "registry must not be null");
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher must not be null");
        this.application = new ExecutionApplication(this);
    }

    public static ExecutionRuntime create(ExecutionRuntimeConfiguration configuration) {
        Objects.requireNonNull(configuration, "configuration must not be null");

        Path root = configuration.storageDirectory();

        AssessmentRuntime assessmentRuntime = AssessmentRuntime.create(
                AssessmentRuntimeConfiguration.fileBacked(root.resolve("assessment")));

        KnowledgeRuntime knowledgeRuntime = KnowledgeRuntime.create(
                KnowledgeRuntimeConfiguration.fileBacked(root.resolve("knowledge")));

        PersistentRuleService ruleService = createRuleService(root.resolve("rules"));
        PublicationRuntime publicationRuntime = PublicationRuntime.fileBacked(root.resolve("publishing"));

        AssessmentApplication assessmentApplication = assessmentRuntime.application();
        PersistentKnowledgeService knowledgeService = knowledgeRuntime.knowledgeService();
        PublicationApplication publicationApplication = publicationRuntime.application();

        InMemoryExecutionAdapterRegistry registry = new InMemoryExecutionAdapterRegistry(List.of(
                new AssessmentExecutionAdapter(assessmentApplication),
                new KnowledgeExecutionAdapter(knowledgeService),
                new RuleEvaluationAdapter(ruleService),
                new PublishingExecutionAdapter(publicationApplication)));

        ExecutionStepDispatcher dispatcher = new ExecutionStepDispatcher(registry);

        return new ExecutionRuntime(
                root,
                assessmentRuntime,
                knowledgeRuntime,
                ruleService,
                publicationRuntime,
                registry,
                dispatcher);
    }

    public static ExecutionRuntime fileBacked(Path storageDirectory) {
        return create(ExecutionRuntimeConfiguration.fileBacked(storageDirectory));
    }

    private static PersistentRuleService createRuleService(Path storageDirectory) {
        FileRulePersistence persistence = new FileRulePersistence(storageDirectory);
        FileRuleRepository repository = new FileRuleRepository(persistence);
        return new PersistentRuleService(repository);
    }

    public Path storageDirectory() {
        return storageDirectory;
    }

    public AssessmentRuntime assessmentRuntime() {
        return assessmentRuntime;
    }

    public KnowledgeRuntime knowledgeRuntime() {
        return knowledgeRuntime;
    }

    public PersistentRuleService ruleService() {
        return ruleService;
    }

    public PublicationRuntime publicationRuntime() {
        return publicationRuntime;
    }

    public ExecutionAdapterRegistry registry() {
        return registry;
    }

    public ExecutionStepDispatcher dispatcher() {
        return dispatcher;
    }

    public ExecutionApplication application() {
        return application;
    }
}
