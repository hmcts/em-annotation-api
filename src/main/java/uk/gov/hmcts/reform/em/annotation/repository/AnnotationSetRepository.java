package uk.gov.hmcts.reform.em.annotation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.em.annotation.domain.AnnotationSet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Spring Data  repository for the AnnotationSet entity.
 */
@Repository
public interface AnnotationSetRepository extends JpaRepository<AnnotationSet, UUID> {

    Optional<AnnotationSet> findByDocumentIdAndCreatedBy(String documentId, String createdBy);

    List<AnnotationSet> findAllByDocumentId(String string);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM AnnotationSet a WHERE a.id IN :ids")
    void deleteAllByIdIn(@Param("ids") List<UUID> ids);
}