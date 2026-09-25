package de.dn.enterprise.platform.orchestration.service;

import de.dn.enterprise.platform.orchestration.domain.Orchestration;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationId;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationMetadata;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStatus;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationStep;
import de.dn.enterprise.platform.orchestration.domain.OrchestrationType;
import de.dn.enterprise.platform.orchestration.spi.OrchestrationRepository;
import de.dn.enterprise.platform.orchestration.validation.OrchestrationValidator;

import java.util.List;
import java.util.Objects;

public final class PersistentOrchestrationService {
    private final OrchestrationRepository repository;
    private final OrchestrationValidator validator;

    public PersistentOrchestrationService(OrchestrationRepository repository) {
        this(repository, new OrchestrationValidator());
    }

    public PersistentOrchestrationService(OrchestrationRepository repository, OrchestrationValidator validator) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.validator = Objects.requireNonNull(validator, "validator must not be null");
    }

    public Orchestration create(OrchestrationType type, OrchestrationMetadata metadata, List<OrchestrationStep> steps) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(metadata, "metadata must not be null");
        Objects.requireNonNull(steps, "steps must not be null");
        Orchestration created = Orchestration.draft(type, metadata, steps);
        validator.validate(created);
        return repository.save(created);
    }

    public Orchestration get(OrchestrationId id) {
        Objects.requireNonNull(id, "id must not be null");
        Orchestration result = repository.findById(id).orElseThrow(() -> new OrchestrationNotFoundException(id));
        validator.validate(result);
        return result;
    }

    public List<Orchestration> findAll() {
        return repository.findAll().stream().peek(validator::validate).toList();
    }

    public List<Orchestration> findByType(OrchestrationType type) {
        Objects.requireNonNull(type, "type must not be null");
        return repository.findByType(type).stream().peek(validator::validate).toList();
    }

    public List<Orchestration> findByStatus(OrchestrationStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return repository.findByStatus(status).stream().peek(validator::validate).toList();
    }

    public Orchestration updateMetadata(OrchestrationId id, OrchestrationMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata must not be null");
        Orchestration current = get(id);
        ensureMetadataChangeAllowed(current);
        validator.validateMetadata(metadata);
        Orchestration updated = replace(current, current.status(), metadata, current.steps());
        validator.validate(updated);
        return repository.save(updated);
    }

    public Orchestration start(OrchestrationId id) { return transition(id, OrchestrationStatus.RUNNING); }
    public Orchestration complete(OrchestrationId id) { return transition(id, OrchestrationStatus.COMPLETED); }
    public Orchestration fail(OrchestrationId id) { return transition(id, OrchestrationStatus.FAILED); }

    private Orchestration transition(OrchestrationId id, OrchestrationStatus target) {
        Orchestration current = get(id);
        if (!isAllowed(current.status(), target)) throw new OrchestrationLifecycleException(current.status(), target);
        Orchestration updated = replace(current, target, current.metadata(), current.steps());
        validator.validate(updated);
        return repository.save(updated);
    }

    private static boolean isAllowed(OrchestrationStatus from, OrchestrationStatus to) {
        return switch (from) {
            case DRAFT -> to == OrchestrationStatus.RUNNING;
            case RUNNING -> to == OrchestrationStatus.COMPLETED || to == OrchestrationStatus.FAILED;
            case COMPLETED, FAILED -> false;
        };
    }

    private static void ensureMetadataChangeAllowed(Orchestration orchestration) {
        if (orchestration.status() != OrchestrationStatus.DRAFT)
            throw new OrchestrationLifecycleException(orchestration.status(), orchestration.status());
    }

    private static Orchestration replace(Orchestration current, OrchestrationStatus status, OrchestrationMetadata metadata, List<OrchestrationStep> steps) {
        return new Orchestration(current.id(), current.type(), status, metadata, steps);
    }
}
