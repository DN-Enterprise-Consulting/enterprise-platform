package de.dn.enterprise.platform.assessment.query;

import de.dn.enterprise.platform.assessment.domain.AssessmentStatus;
import de.dn.enterprise.platform.assessment.domain.AssessmentType;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class AssessmentQueryTest {
 @Test void allQueryHasNoFilters(){ var q=AssessmentQuery.all(); assertThat(q.type()).isEmpty(); assertThat(q.status()).isEmpty(); }
 @Test void typeQueryContainsTypeOnly(){ var q=AssessmentQuery.byType(AssessmentType.ENTERPRISE_ARCHITECTURE); assertThat(q.type()).contains(AssessmentType.ENTERPRISE_ARCHITECTURE); assertThat(q.status()).isEmpty(); }
 @Test void statusQueryContainsStatusOnly(){ var q=AssessmentQuery.byStatus(AssessmentStatus.COMPLETED); assertThat(q.type()).isEmpty(); assertThat(q.status()).contains(AssessmentStatus.COMPLETED); }
 @Test void combinedQueryContainsBothFilters(){ var q=AssessmentQuery.byTypeAndStatus(AssessmentType.SOFTWARE_ARCHITECTURE, AssessmentStatus.IN_PROGRESS); assertThat(q.type()).contains(AssessmentType.SOFTWARE_ARCHITECTURE); assertThat(q.status()).contains(AssessmentStatus.IN_PROGRESS); }
}
