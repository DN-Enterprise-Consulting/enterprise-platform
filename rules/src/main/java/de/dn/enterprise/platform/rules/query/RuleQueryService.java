package de.dn.enterprise.platform.rules.query;

import de.dn.enterprise.platform.rules.domain.Rule;
import de.dn.enterprise.platform.rules.spi.RuleRepository;

import java.util.List;
import java.util.Objects;

public final class RuleQueryService {

    private final RuleRepository repository;

    public RuleQueryService(RuleRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    public List<Rule> query(RuleQuery query) {
        Objects.requireNonNull(query, "query must not be null");

        return repository.findAll().stream()
                .filter(rule -> query.type().map(type -> rule.type() == type).orElse(true))
                .filter(rule -> query.status().map(status -> rule.status() == status).orElse(true))
                .toList();
    }
}
