package uk.gov.hmcts.reform.em.annotation.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.em.annotation.rest.errors.BadRequestAlertException;
import uk.gov.hmcts.reform.em.annotation.rest.util.HeaderUtil;
import uk.gov.hmcts.reform.em.annotation.rest.util.PaginationUtil;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationService;
import uk.gov.hmcts.reform.em.annotation.service.CcdService;
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
@Tag(name = "Annotations Service", description = "Endpoint for managing Annotations.")
public class AnnotationResource {

    private final Logger log = LoggerFactory.getLogger(AnnotationResource.class);

    private static final String ENTITY_NAME = "annotation";

    private final AnnotationService annotationService;

    private final CcdService ccdService;

    public AnnotationResource(AnnotationService annotationService, CcdService ccdService) {
        this.annotationService = annotationService;
        this.ccdService = ccdService;
    }

    /**
     * POST  /annotations : Create a new annotation.
     *
     * @param annotationDTO the annotationDTO to create
     * @return the ResponseEntity with status "201" (Created) and with body
     *      the new annotationDTO, or with status "400" (Bad Request) if the annotation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Operation(summary = "Create an annotationDTO", description = "A POST request to create an annotationDTO",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "authorization",
                            description = "Authorization (Idam Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                            description = "Service Authorization (S2S Bearer token)", required = true,
                            schema = @Schema(type = "string"))})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "400", description = "annotationDTO not valid, invalid id"),
            @ApiResponse(responseCode = "401", description = "Unauthorised"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @PostMapping("/annotations")
    public ResponseEntity<AnnotationDTO> createAnnotation(HttpServletRequest request, @RequestBody AnnotationDTO annotationDTO) throws URISyntaxException {
        log.debug("REST request to save Annotation : {}", annotationDTO);
        if (annotationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        annotationDTO.setCommentHeader(
                ccdService.buildCommentHeader(annotationDTO, request.getHeader("Authorization")));

        log.info("annotationDTO value is {}", annotationDTO);
        try {
            annotationService.save(annotationDTO);
        } catch (PSQLException | ConstraintViolationException | DataIntegrityViolationException exception) {
            log.error("REST request to create and save Annotation had issue : {}", annotationDTO);
            return ResponseEntity.badRequest().build();
        }


        final URI uri = new URI("/api/annotations/" + annotationDTO.getId());
        return annotationService.findOne(annotationDTO.getId(), true).map(renderedAnnotation ->
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
     * @return the ResponseEntity with status "200" (OK) and with body the updated annotationDTO,
     *      or with status "400" (Bad Request) if the annotationDTO is not valid,
     *      or with status "500" (Internal Server Error) if the annotationDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Operation(summary = "Update an existing annotationDTO", description = "A PUT request to update an annotationDTO",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "authorization",
                            description = "Authorization (Idam Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                            description = "Service Authorization (S2S Bearer token)", required = true,
                            schema = @Schema(type = "string"))})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "annotationDTO not valid, invalid id"),
            @ApiResponse(responseCode = "500", description = "annotationDTO couldn't be updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorised"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @PutMapping("/annotations")
    public ResponseEntity<AnnotationDTO> updateAnnotation(@RequestBody AnnotationDTO annotationDTO) throws URISyntaxException {
        log.debug("REST request to update Annotation : {}", annotationDTO);
        if (annotationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        AnnotationDTO result = null;
        try {
            result = annotationService.save(annotationDTO);
        } catch (PSQLException | ConstraintViolationException | DataIntegrityViolationException exception) {
            log.error("REST request to update and save Annotation had issue : {}", annotationDTO);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, annotationDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /annotations : get all the annotations.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status "200" (OK) and the list of annotations in body
     */
    @Operation(summary = "Get all annotations", description = "A GET request without a body is used to retrieve all annotations",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "authorization",
                            description = "Authorization (Idam Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                            description = "Service Authorization (S2S Bearer token)", required = true,
                            schema = @Schema(type = "string"))})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorised"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @GetMapping("/annotations")
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
     * @return the ResponseEntity with status "200" (OK) and with body the annotationDTO, or with status "404" (Not Found)
     */
    @Operation(summary = "Get an existing annotationDTO", description = "A GET request to retrieve an annotationDTO",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "authorization",
                            description = "Authorization (Idam Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                            description = "Service Authorization (S2S Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.PATH, name = "id",
                            description = "Annotation Id", required = true,
                            schema = @Schema(type = "UUID"))})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorised"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @GetMapping("/annotations/{id}")
    public ResponseEntity<AnnotationDTO> getAnnotation(@PathVariable UUID id) {
        log.debug("REST request to get Annotation : {}", id);
        Optional<AnnotationDTO> annotationDTO = annotationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(annotationDTO);
    }

    /**
     * DELETE  /annotations/:id : delete the "id" annotation.
     *
     * @param id the id of the annotationDTO to delete
     * @return the ResponseEntity with status "200" (OK)
     */
    @Operation(summary = "Delete an annotationDTO", description = "A DELETE request to delete an annotationDTO",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "authorization",
                            description = "Authorization (Idam Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                            description = "Service Authorization (S2S Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.PATH, name = "id",
                            description = "Annotation Id", required = true,
                            schema = @Schema(type = "UUID"))})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorised"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @DeleteMapping("/annotations/{id}")
    public ResponseEntity<Void> deleteAnnotation(@PathVariable UUID id) {
        log.debug("REST request to delete Annotation : {}", id);
        annotationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
