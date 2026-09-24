package de.dn.enterprise.platform.knowledge.spi;

import de.dn.enterprise.platform.knowledge.domain.KnowledgeObject;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectId;
import de.dn.enterprise.platform.knowledge.domain.KnowledgeObjectType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeRepositoryContractTest {

    @Test
    void exposesRequiredRepositoryOperations() throws Exception {
        assertMethod("save", KnowledgeObject.class, KnowledgeObject.class);
        assertMethod("findById", Optional.class, KnowledgeObjectId.class);
        assertMethod("findByType", List.class, KnowledgeObjectType.class);
        assertMethod("findAll", List.class);
        assertMethod("existsById", boolean.class, KnowledgeObjectId.class);
    }

    private static void assertMethod(
            String name,
            Class<?> returnType,
            Class<?>... parameterTypes) throws Exception {
        Method method = KnowledgeRepository.class.getMethod(name, parameterTypes);
        assertThat(method.getReturnType()).isEqualTo(returnType);
    }
}
