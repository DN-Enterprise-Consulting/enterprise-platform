package de.dn.enterprise.platform.rules.query;

import de.dn.enterprise.platform.rules.domain.RuleStatus;
import de.dn.enterprise.platform.rules.domain.RuleType;

import java.util.Optional;

public record RuleQuery(
        Optional<RuleType> type,
        Optional<RuleStatus> status) {

    public RuleQuery {
        type = type == null ? Optional.empty() : type;
        status = status == null ? Optional.empty() : status;
    }

    public static RuleQuery all() {
        return new RuleQuery(Optional.empty(), Optional.empty());
    }

    public static RuleQuery byType(RuleType type) {
        return new RuleQuery(Optional.of(type), Optional.empty());
    }

    public static RuleQuery byStatus(RuleStatus status) {
        return new RuleQuery(Optional.empty(), Optional.of(status));
    }

    public static RuleQuery byTypeAndStatus(RuleType type, RuleStatus status) {
        return new RuleQuery(Optional.of(type), Optional.of(status));
    }
}
