package uk.gov.hmcts.reform.em.annotation.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;

import java.util.Optional;

/**
 * Service Interface for managing Annotation.
 */
public interface AnnotationService {

    /**
     * Save a annotation.
     *
     * @param annotationDTO the entity to save
     * @return the persisted entity
     */
    AnnotationDTO save(AnnotationDTO annotationDTO);

    /**
     * Get all the annotations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<AnnotationDTO> findAll(Pageable pageable);


    /**
     * Get the "id" annotation.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<AnnotationDTO> findOne(Long id);

    /**
     * Delete the "id" annotation.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
