package de.dn.enterprise.platform.execution.rules;

import de.dn.enterprise.platform.execution.domain.ExecutionContext;
import de.dn.enterprise.platform.execution.domain.ExecutionResult;
import de.dn.enterprise.platform.execution.spi.ExecutionAdapter;
import de.dn.enterprise.platform.rules.domain.Rule;
import de.dn.enterprise.platform.rules.domain.RuleMetadata;
import de.dn.enterprise.platform.rules.domain.RuleType;
import de.dn.enterprise.platform.rules.service.PersistentRuleService;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Execution adapter for the Rules domain.
 *
 * The current Rules baseline provides rule lifecycle and persistence services,
 * but no expression evaluator. Therefore this adapter establishes the
 * execution boundary by creating and activating the requested rule and
 * returning its executable rule definition as part of the result.
 */
public final class RuleEvaluationAdapter implements ExecutionAdapter {

    public static final String DOMAIN_KEY = "rules";
    public static final String TYPE_KEY = "rule.type";
    public static final String NAME_KEY = "rule.name";
    public static final String DESCRIPTION_KEY = "rule.description";
    public static final String DEFINITION_KEY = "rule.definition";

    private final PersistentRuleService service;

    public RuleEvaluationAdapter(PersistentRuleService service) {
        this.service = Objects.requireNonNull(service, "service must not be null");
    }

    @Override
    public String domainKey() {
        return DOMAIN_KEY;
    }

    @Override
    public ExecutionResult execute(ExecutionContext context) {
        Objects.requireNonNull(context, "context must not be null");

        RuleType type = required(context, TYPE_KEY)
                .map(RuleType::valueOf)
                .orElseThrow();
        String name = required(context, NAME_KEY).orElseThrow();
        String description = required(context, DESCRIPTION_KEY).orElse("");
        String definition = required(context, DEFINITION_KEY).orElseThrow();

        Rule rule = service.create(
                type,
                new RuleMetadata(name, description, Map.of()),
                definition);

        rule = service.activate(rule.id());

        return ExecutionResult.success(
                "rule evaluation execution completed",
                Map.of(
                        "ruleId", rule.id().value().toString(),
                        "ruleType", rule.type().name(),
                        "ruleStatus", rule.status().name(),
                        "ruleDefinition", rule.definition()));
    }

    private Optional<String> required(ExecutionContext context, String key) {
        return context.get(key).filter(value -> !value.isBlank());
    }
}
