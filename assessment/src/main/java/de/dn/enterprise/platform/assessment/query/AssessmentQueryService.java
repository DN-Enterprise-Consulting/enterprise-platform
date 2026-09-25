package de.dn.enterprise.platform.assessment.query;

import de.dn.enterprise.platform.assessment.domain.Assessment;
import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import de.dn.enterprise.platform.assessment.spi.AssessmentRepository;
import java.util.List;
import java.util.Objects;

public final class AssessmentQueryService {
    private final AssessmentRepository repository;
    public AssessmentQueryService(AssessmentRepository repository) { this.repository = Objects.requireNonNull(repository, "repository must not be null"); }
    public List<Assessment> query(AssessmentQuery query) {
        Objects.requireNonNull(query, "query must not be null");
        List<Assessment> result = repository.findAll();
        if (query.type().isPresent()) { var type = query.type().get(); result = result.stream().filter(a -> a.type() == type).toList(); }
        if (query.status().isPresent()) { var status = query.status().get(); result = result.stream().filter(a -> a.status() == status).toList(); }
        return result;
    }
    public List<Assessment> findAll() { return query(AssessmentQuery.all()); }
    public List<Assessment> findByType(AssessmentType type) { return query(AssessmentQuery.byType(type)); }
    public List<Assessment> findByStatus(AssessmentStatus status) { return query(AssessmentQuery.byStatus(status)); }
    public List<Assessment> findByTypeAndStatus(AssessmentType type, AssessmentStatus status) { return query(AssessmentQuery.byTypeAndStatus(type, status)); }
}
