package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.domain.Annotation;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationRepository;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationSetRepository;
import uk.gov.hmcts.reform.em.annotation.repository.CommentRepository;
import uk.gov.hmcts.reform.em.annotation.repository.RectangleRepository;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.AnnotationMapper;

import java.util.Optional;

/**
 * Service Implementation for managing Annotation.
 */
@Service
@Transactional
public class AnnotationServiceImpl implements AnnotationService {

    private final Logger log = LoggerFactory.getLogger(AnnotationServiceImpl.class);

    private final AnnotationRepository annotationRepository;

    private final AnnotationMapper annotationMapper;
    private final RectangleRepository rectangleRepository;
    private final CommentRepository commentRepository;
    private final AnnotationSetRepository annotationSetRepository;

    public AnnotationServiceImpl(AnnotationRepository annotationRepository, AnnotationMapper annotationMapper, RectangleRepository rectangleRepository, CommentRepository commentRepository, AnnotationSetRepository annotationSetRepository) {
        this.annotationRepository = annotationRepository;
        this.annotationMapper = annotationMapper;
        this.rectangleRepository = rectangleRepository;
        this.commentRepository = commentRepository;
        this.annotationSetRepository = annotationSetRepository;
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
    public Optional<AnnotationDTO> findOne(Long id) {
        log.debug("Request to get Annotation : {}", id);
        return annotationRepository.findById(id)
            .map(annotationMapper::toDto);
    }

    /**
     * Delete the annotation by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Annotation : {}", id);
        annotationRepository.deleteById(id);
    }
}
