package uk.gov.hmcts.reform.em.annotation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.em.annotation.domain.Annotation;


/**
 * Spring Data  repository for the Annotation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Long> {

}
