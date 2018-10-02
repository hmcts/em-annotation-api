package uk.gov.hmcts.reform.em.annotation.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.gov.hmcts.reform.em.annotation.service.dto.RectangleDTO;

import java.util.Optional;
import java.util.UUID;

/**
 * Service Interface for managing Rectangle.
 */
public interface RectangleService {

    /**
     * Save a rectangle.
     *
     * @param rectangleDTO the entity to save
     * @return the persisted entity
     */
    RectangleDTO save(RectangleDTO rectangleDTO);

    /**
     * Get all the rectangles.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<RectangleDTO> findAll(Pageable pageable);


    /**
     * Get the "id" rectangle.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<RectangleDTO> findOne(UUID id);

    /**
     * Delete the "id" rectangle.
     *
     * @param id the id of the entity
     */
    void delete(UUID id);
}
