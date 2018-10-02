package uk.gov.hmcts.reform.em.annotation.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationSetDTO;

import java.util.Optional;
import java.util.UUID;

/**
 * Service Interface for managing AnnotationSet.
 */
public interface AnnotationSetService {

    /**
     * Save a annotationSet.
     *
     * @param annotationSetDTO the entity to save
     * @return the persisted entity
     */
    AnnotationSetDTO save(AnnotationSetDTO annotationSetDTO);

    /**
     * Get all the annotationSets.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<AnnotationSetDTO> findAll(Pageable pageable);


    /**
     * Get the "id" annotationSet.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<AnnotationSetDTO> findOne(UUID id);

    /**
     * Delete the "id" annotationSet.
     *
     * @param id the id of the entity
     */
    void delete(UUID id);


    Optional<AnnotationSetDTO> findOneByDocumentId(String documentId);
}
