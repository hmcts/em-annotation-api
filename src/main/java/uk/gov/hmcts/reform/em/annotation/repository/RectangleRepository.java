package uk.gov.hmcts.reform.em.annotation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.em.annotation.domain.Rectangle;

import java.util.List;
import java.util.UUID;


/**
 * Spring Data  repository for the Rectangle entity.
 */
@Repository
public interface RectangleRepository extends JpaRepository<Rectangle, UUID> {

    @Query("SELECT r.id FROM Rectangle r WHERE r.annotation.id IN :annotationIds")
    List<UUID> findAllIdsByAnnotationIdIn(@Param("annotationIds") List<UUID> annotationIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Rectangle r WHERE r.id IN :ids")
    void deleteAllByIdIn(@Param("ids") List<UUID> ids);
}