package uk.gov.hmcts.reform.em.annotation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.em.annotation.domain.Rectangle;

import java.util.UUID;


/**
 * Spring Data  repository for the Rectangle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RectangleRepository extends JpaRepository<Rectangle, UUID> {

}
