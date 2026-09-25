package de.dn.enterprise.platform.rules.spi;

import de.dn.enterprise.platform.rules.domain.Rule;
import de.dn.enterprise.platform.rules.domain.RuleId;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RulePersistenceContractTest {

    @Test
    void exposesStablePersistenceContract() throws NoSuchMethodException {
        Method save = RulePersistence.class.getMethod("save", Rule.class);
        Method load = RulePersistence.class.getMethod("load", RuleId.class);
        Method exists = RulePersistence.class.getMethod("exists", RuleId.class);
        Method delete = RulePersistence.class.getMethod("delete", RuleId.class);

        assertThat(save.getReturnType()).isEqualTo(Rule.class);
        assertThat(load.getReturnType()).isEqualTo(Optional.class);
        assertThat(exists.getReturnType()).isEqualTo(boolean.class);
        assertThat(delete.getReturnType()).isEqualTo(void.class);
    }
}
