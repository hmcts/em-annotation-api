package uk.gov.hmcts.reform.em.annotation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.em.annotation.domain.Comment;

import java.util.List;
import java.util.UUID;


/**
 * Spring Data  repository for the Comment entity.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Query("SELECT c.id FROM Comment c WHERE c.annotation.id IN :annotationIds")
    List<UUID> findAllIdsByAnnotationIdIn(@Param("annotationIds") List<UUID> annotationIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Comment c WHERE c.id IN :ids")
    void deleteAllByIdIn(@Param("ids") List<UUID> ids);
}