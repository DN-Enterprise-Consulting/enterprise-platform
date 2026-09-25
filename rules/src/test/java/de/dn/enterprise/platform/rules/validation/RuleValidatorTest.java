package de.dn.enterprise.platform.rules.validation;

import de.dn.enterprise.platform.rules.domain.Rule;
import de.dn.enterprise.platform.rules.domain.RuleId;
import de.dn.enterprise.platform.rules.domain.RuleMetadata;
import de.dn.enterprise.platform.rules.domain.RuleStatus;
import de.dn.enterprise.platform.rules.domain.RuleType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RuleValidatorTest {

    private final RuleValidator validator = new RuleValidator();

    @Test
    void acceptsValidRule() {
        assertThatCode(() -> validator.validate(rule("valid definition")))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsNullRule() {
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(RuleValidationException.class);
    }

    private static Rule rule(String definition) {
        return new Rule(
                RuleId.newId(),
                RuleType.values()[0],
                RuleStatus.DRAFT,
                new RuleMetadata("Rule", "Description", Map.of()),
                definition);
    }
}
