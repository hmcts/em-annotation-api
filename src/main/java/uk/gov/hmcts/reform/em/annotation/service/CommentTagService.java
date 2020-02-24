package uk.gov.hmcts.reform.em.annotation.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.gov.hmcts.reform.em.annotation.service.dto.CommentTagDTO;

import java.util.Optional;

/**
 * Service Interface for managing Comment Tag.
 */
public interface CommentTagService {

    /**
     * Save a comment tag.
     *
     * @param commentTagDTO the entity to save
     * @return the persisted entity
     */
    CommentTagDTO save(CommentTagDTO commentTagDTO);

    /**
     * Get all the comment tags.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<CommentTagDTO> findAll(Pageable pageable);


    /**
     * Get the "id" comment tag.
     *
     * @param name the id of the entity
     * @return the entity
     */
    Optional<CommentTagDTO> findOne(String name);
}
