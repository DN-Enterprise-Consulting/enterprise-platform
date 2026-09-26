package de.dn.enterprise.platform.search.spi;

import de.dn.enterprise.platform.search.domain.SearchDocument;
import de.dn.enterprise.platform.search.domain.SearchDocumentId;
import de.dn.enterprise.platform.search.domain.SearchDocumentStatus;
import de.dn.enterprise.platform.search.domain.SearchDocumentType;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SearchDocumentRepositoryContractTest {
    @Test
    void exposesExpectedRepositoryContract() throws Exception {
        Class<SearchDocumentRepository> type = SearchDocumentRepository.class;

        Method save = type.getMethod("save", SearchDocument.class);
        assertThat(save.getReturnType()).isEqualTo(SearchDocument.class);

        Method findById = type.getMethod("findById", SearchDocumentId.class);
        assertThat(findById.getReturnType()).isEqualTo(SearchDocument.class);

        Method findAll = type.getMethod("findAll");
        assertListOf(findAll, SearchDocument.class);

        Method findByType = type.getMethod("findByType", SearchDocumentType.class);
        assertListOf(findByType, SearchDocument.class);

        Method findByStatus = type.getMethod("findByStatus", SearchDocumentStatus.class);
        assertListOf(findByStatus, SearchDocument.class);

        Method existsById = type.getMethod("existsById", SearchDocumentId.class);
        assertThat(existsById.getReturnType()).isEqualTo(boolean.class);

        Method deleteById = type.getMethod("deleteById", SearchDocumentId.class);
        assertThat(deleteById.getReturnType()).isEqualTo(boolean.class);
    }

    private static void assertListOf(Method method, Class<?> elementType) {
        Type genericReturnType = method.getGenericReturnType();
        assertThat(genericReturnType).isInstanceOf(ParameterizedType.class);
        ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
        assertThat(parameterizedType.getRawType()).isEqualTo(List.class);
        assertThat(parameterizedType.getActualTypeArguments()).containsExactly(elementType);
    }
}
