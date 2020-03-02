package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.domain.Annotation;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationRepository;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationService;
import uk.gov.hmcts.reform.em.annotation.service.TagService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.TagDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.AnnotationMapper;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.Optional;
import java.util.UUID;

/**
 * Service Implementation for managing Annotation.
 */
@Service
@Transactional
public class AnnotationServiceImpl implements AnnotationService {

    private final Logger log = LoggerFactory.getLogger(AnnotationServiceImpl.class);

    private final AnnotationRepository annotationRepository;

    private final AnnotationMapper annotationMapper;

    private final TagService tagService;

    @PersistenceContext
    private final EntityManager entityManager;

    public AnnotationServiceImpl(AnnotationRepository annotationRepository,
                                 AnnotationMapper annotationMapper,
                                 TagService tagService,
                                 EntityManager entityManager) {
        this.annotationRepository = annotationRepository;
        this.annotationMapper = annotationMapper;
        this.tagService = tagService;
        this.entityManager = entityManager;
    }

    /**
     * Save a annotation.
     *
     * @param annotationDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public AnnotationDTO save(AnnotationDTO annotationDTO) {
        log.debug("Request to save Annotation : {}", annotationDTO);

        for (TagDTO tag : annotationDTO.getTags()) {
            tag.setCreatedBy(annotationDTO.getCreatedBy());
        }

        final Annotation annotation = annotationMapper.toEntity(annotationDTO);

        if (annotationDTO.getRectangles() != null) {
            annotation.getRectangles().forEach(r -> {
                if (r.getAnnotation() == null ) {
                    r.setAnnotation(annotation);
                }
            });
        }
        if (annotationDTO.getComments() != null) {
            annotation.getComments().forEach(r -> {
                if (r.getAnnotation() == null ) {
                    r.setAnnotation(annotation);
                }
            });
        }
        if (annotationDTO.getTags() != null) {
            annotation.getTags().forEach(t -> {
                if (t.getId() == null) {
                    tagService.persistTag(t);
                    t.setId(UUID.randomUUID());
                }

                if (t.getAnnotation() == null) {
                    t.setAnnotation(annotation);
                }
                t.setCreatedBy(annotationDTO.getCreatedBy());
            });
        }

        return annotationMapper.toDto(annotationRepository.save(annotation));
    }

    /**
     * Get all the annotations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AnnotationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Annotations");
        return annotationRepository.findAll(pageable)
            .map(annotationMapper::toDto);
    }


    /**
     * Get one annotation by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<AnnotationDTO> findOne(UUID id) {
        log.debug("Request to get Annotation : {}", id);
        return findOne(id, false);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AnnotationDTO> findOne(UUID id, boolean refresh) {
        Optional<Annotation> annotation = annotationRepository.findById(id);
        if (refresh) {
            annotation.ifPresent( a -> {
                try {
                    entityManager.refresh(a);
                } catch (EntityNotFoundException e) {
                    log.debug("entity not found", e);
                }
            });
        }
        return annotation.map(annotationMapper::toDto);
    }

    /**
     * Delete the annotation by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(UUID id) {
        log.debug("Request to delete Annotation : {}", id);
        annotationRepository.deleteById(id);
    }
}
