package uk.gov.hmcts.reform.em.annotation.service;

import org.postgresql.util.PSQLException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;

import java.util.Optional;
import java.util.UUID;

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
    AnnotationDTO save(AnnotationDTO annotationDTO) throws PSQLException;

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
    Optional<AnnotationDTO> findOne(UUID id);

    Optional<AnnotationDTO> findOne(UUID id, boolean refresh);

    /**
     * Delete the "id" annotation.
     *
     * @param id the id of the entity
     */
    void delete(UUID id);


}
