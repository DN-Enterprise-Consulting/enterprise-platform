package de.dn.enterprise.platform.assessment.query;

import de.dn.enterprise.platform.assessment.domain.*;
import de.dn.enterprise.platform.assessment.spi.AssessmentRepository;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

class AssessmentQueryServiceTest {
 private final Assessment first=Assessment.draft(AssessmentType.ENTERPRISE_ARCHITECTURE,new AssessmentMetadata("Enterprise Architecture","first",Map.of()));
 private final Assessment second=Assessment.draft(AssessmentType.SOFTWARE_ARCHITECTURE,new AssessmentMetadata("Software Architecture","second",Map.of()));
 private final AssessmentRepository repository=new Stub(List.of(first,second));
 private final AssessmentQueryService service=new AssessmentQueryService(repository);
 @Test void findsAll(){assertThat(service.findAll()).containsExactly(first,second);}
 @Test void findsByType(){assertThat(service.findByType(AssessmentType.SOFTWARE_ARCHITECTURE)).containsExactly(second);}
 @Test void findsByStatus(){assertThat(service.findByStatus(AssessmentStatus.DRAFT)).containsExactly(first,second);}
 @Test void combinesTypeAndStatus(){var active=first.withStatus(AssessmentStatus.IN_PROGRESS); var s=new AssessmentQueryService(new Stub(List.of(active,second))); assertThat(s.findByTypeAndStatus(AssessmentType.ENTERPRISE_ARCHITECTURE,AssessmentStatus.IN_PROGRESS)).containsExactly(active);}
 @Test void rejectsNullRepository(){assertThatThrownBy(()->new AssessmentQueryService(null)).isInstanceOf(NullPointerException.class);}
 @Test void rejectsNullQuery(){assertThatThrownBy(()->service.query(null)).isInstanceOf(NullPointerException.class);}
 static final class Stub implements AssessmentRepository { final List<Assessment> a; Stub(List<Assessment> a){this.a=List.copyOf(a);} public Assessment save(Assessment x){throw new UnsupportedOperationException();} public Optional<Assessment> findById(AssessmentId id){return a.stream().filter(x->x.id().equals(id)).findFirst();} public List<Assessment> findAll(){return a;} public List<Assessment> findByType(AssessmentType t){return a.stream().filter(x->x.type()==t).toList();} public List<Assessment> findByStatus(AssessmentStatus s){return a.stream().filter(x->x.status()==s).toList();} public boolean existsById(AssessmentId id){return findById(id).isPresent();} }
}
