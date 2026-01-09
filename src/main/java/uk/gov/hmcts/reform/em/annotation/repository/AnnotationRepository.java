package uk.gov.hmcts.reform.em.annotation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.em.annotation.domain.Annotation;

import java.util.List;
import java.util.UUID;


/**
 * Spring Data  repository for the Annotation entity.
 */
@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, UUID> {

    @Query("SELECT a.id FROM Annotation a WHERE a.annotationSet.id IN :setIds")
    List<UUID> findAllIdsByAnnotationSetIdIn(@Param("setIds") List<UUID> setIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Annotation a WHERE a.id IN :ids")
    void deleteAllByIdIn(@Param("ids") List<UUID> ids);
}