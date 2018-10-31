package uk.gov.hmcts.reform.em.annotation.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.em.annotation.rest.errors.BadRequestAlertException;
import uk.gov.hmcts.reform.em.annotation.rest.util.HeaderUtil;
import uk.gov.hmcts.reform.em.annotation.rest.util.PaginationUtil;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing Annotation.
 */
@RestController
@RequestMapping("/api")
public class AnnotationResource {

    private final Logger log = LoggerFactory.getLogger(AnnotationResource.class);

    private static final String ENTITY_NAME = "annotation";

    private final AnnotationService annotationService;

    public AnnotationResource(AnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    /**
     * POST  /annotations : Create a new annotation.
     *
     * @param annotationDTO the annotationDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new annotationDTO, or with status 400 (Bad Request) if the annotation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/annotations")
    //@Timed
    public ResponseEntity<AnnotationDTO> createAnnotation(@RequestBody AnnotationDTO annotationDTO) throws URISyntaxException {
        log.debug("REST request to save Annotation : {}", annotationDTO);
        if (annotationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        annotationService.save(annotationDTO);

        final URI uri = new URI("/api/annotations/" + annotationDTO.getId());
        return annotationService.findOne(annotationDTO.getId()).map( renderedAnnotation ->
                ResponseEntity.created(uri)
                        .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, annotationDTO.getId().toString()))
                        .body(renderedAnnotation)
                )
                .orElse(ResponseEntity.badRequest().build());
    }

    /**
     * PUT  /annotations : Updates an existing annotation.
     *
     * @param annotationDTO the annotationDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated annotationDTO,
     * or with status 400 (Bad Request) if the annotationDTO is not valid,
     * or with status 500 (Internal Server Error) if the annotationDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/annotations")
    //@Timed
    public ResponseEntity<AnnotationDTO> updateAnnotation(@RequestBody AnnotationDTO annotationDTO) throws URISyntaxException {
        log.debug("REST request to update Annotation : {}", annotationDTO);
        if (annotationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        AnnotationDTO result = annotationService.save(annotationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, annotationDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /annotations : get all the annotations.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of annotations in body
     */
    @GetMapping("/annotations")
    //@Timed
    public ResponseEntity<List<AnnotationDTO>> getAllAnnotations(Pageable pageable) {
        log.debug("REST request to get a page of Annotations");
        Page<AnnotationDTO> page = annotationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/annotations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /annotations/:id : get the "id" annotation.
     *
     * @param id the id of the annotationDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the annotationDTO, or with status 404 (Not Found)
     */
    @GetMapping("/annotations/{id}")
    //@Timed
    public ResponseEntity<AnnotationDTO> getAnnotation(@PathVariable UUID id) {
        log.debug("REST request to get Annotation : {}", id);
        Optional<AnnotationDTO> annotationDTO = annotationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(annotationDTO);
    }

    /**
     * DELETE  /annotations/:id : delete the "id" annotation.
     *
     * @param id the id of the annotationDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/annotations/{id}")
    //@Timed
    public ResponseEntity<Void> deleteAnnotation(@PathVariable UUID id) {
        log.debug("REST request to delete Annotation : {}", id);
        annotationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
