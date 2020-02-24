package uk.gov.hmcts.reform.em.annotation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.em.annotation.domain.CommentTag;

import java.util.Optional;
import java.util.UUID;

/**
* Spring Data repository for the Comment Tag entity.
*/
public interface CommentTagRepository extends JpaRepository<CommentTag, UUID> {
    Optional<CommentTag> findByName(String name);
}
