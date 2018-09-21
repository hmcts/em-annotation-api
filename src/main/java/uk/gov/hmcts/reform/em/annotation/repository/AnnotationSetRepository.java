package uk.gov.hmcts.reform.em.annotation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.em.annotation.domain.AnnotationSet;

import java.util.Optional;


/**
 * Spring Data  repository for the AnnotationSet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnnotationSetRepository extends JpaRepository<AnnotationSet, Long> {

    Optional<AnnotationSet> findByDocumentIdAndCreatedBy(String documentId, String createdBy);

}
