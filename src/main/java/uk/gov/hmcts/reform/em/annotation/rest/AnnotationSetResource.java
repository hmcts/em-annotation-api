package uk.gov.hmcts.reform.em.annotation.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.em.annotation.config.Constants;
import uk.gov.hmcts.reform.em.annotation.rest.errors.BadRequestAlertException;
import uk.gov.hmcts.reform.em.annotation.rest.util.HeaderUtil;
import uk.gov.hmcts.reform.em.annotation.rest.util.PaginationUtil;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationSetService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationSetDTO;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing AnnotationSet.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "AnnotationSet Service", description = "Endpoint for managing AnnotationSet.")
public class AnnotationSetResource {

    private final Logger log = LoggerFactory.getLogger(AnnotationSetResource.class);

    private static final String ENTITY_NAME = "annotationSet";

    private final AnnotationSetService annotationSetService;

    public AnnotationSetResource(AnnotationSetService annotationSetService) {
        this.annotationSetService = annotationSetService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(Constants.IS_ADMIN);
    }

    /**
     * POST  /annotation-sets : Create a new annotationSet.
     *
     * @param annotationSetDTO the annotationSetDTO to create
     * @return the ResponseEntity with status "201" (Created) and with body the new annotationSetDTO,
     *      or with status "400" (Bad Request) if the annotationSet has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Operation(summary = "Create an annotationSetDTO", description = "A POST request to create an annotationSetDTO",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "authorization",
                            description = "Authorization (Idam Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                            description = "Service Authorization (S2S Bearer token)", required = true,
                            schema = @Schema(type = "string"))})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "400", description = "annotationSetDTO not valid, invalid id"),
            @ApiResponse(responseCode = "401", description = "Unauthorised"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })

    @PostMapping("/annotation-sets")
    public ResponseEntity<AnnotationSetDTO> createAnnotationSet(@RequestBody AnnotationSetDTO annotationSetDTO) throws URISyntaxException {
        log.debug("REST request to save AnnotationSet : {}", annotationSetDTO);
        if (annotationSetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        annotationSetService.save(annotationSetDTO);
        final URI uri = new URI("/api/annotation-sets/" + annotationSetDTO.getId());

        return annotationSetService.findOne(annotationSetDTO.getId()).map(renderedAnnotationSet ->
                ResponseEntity.created(uri)
                    .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, annotationSetDTO.getId().toString()))
                    .body(renderedAnnotationSet)
                )
                .orElse(ResponseEntity.badRequest().build());
    }

    /**
     * PUT  /annotation-sets : Updates an existing annotationSet.
     *
     * @param annotationSetDTO the annotationSetDTO to update
     * @return the ResponseEntity with status "200" (OK) and with body the updated annotationSetDTO,
     *      or with status "400" (Bad Request) if the annotationSetDTO is not valid,
     *      or with status "500" (Internal Server Error) if the annotationSetDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Operation(summary = "Update an existing annotationSetDTO", description = "A PUT request to update "
            + "an annotationSetDTO",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "authorization",
                            description = "Authorization (Idam Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                            description = "Service Authorization (S2S Bearer token)", required = true,
                            schema = @Schema(type = "string"))})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "annotationSetDTO not valid, invalid id"),
            @ApiResponse(responseCode = "500", description = "annotationSetDTO couldn't be updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorised"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @PutMapping("/annotation-sets")
    public ResponseEntity<AnnotationSetDTO> updateAnnotationSet(@RequestBody AnnotationSetDTO annotationSetDTO) throws URISyntaxException {
        log.debug("REST request to update AnnotationSet : {}", annotationSetDTO);
        if (annotationSetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        AnnotationSetDTO result = annotationSetService.save(annotationSetDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, annotationSetDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /annotation-sets : get all the annotationSets.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status "200" (OK) and the list of annotationSets in body
     */
    @Operation(summary = "Get all annotationSets", description = "A GET request without a body is used to retrieve "
            + "all annotationSets",
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
    @GetMapping("/annotation-sets")
    public ResponseEntity<List<AnnotationSetDTO>> getAllAnnotationSets(Pageable pageable) {
        log.debug("REST request to get a page of AnnotationSets");
        Page<AnnotationSetDTO> page = annotationSetService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/annotation-sets");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /annotation-sets/:id : get the "id" annotationSet.
     *
     * @param id the id of the annotationSetDTO to retrieve
     * @return the ResponseEntity with status "200" (OK) and with body the annotationSetDTO, or with status "404" (Not Found)
     */
    @Operation(summary = "Get an existing annotationSetDTO", description = "A GET request to retrieve "
            + "an annotationSetDTO",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "authorization",
                            description = "Authorization (Idam Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                            description = "Service Authorization (S2S Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.PATH, name = "id",
                            description = "AnnotationSet Id", required = true,
                            schema = @Schema(type = "UUID"))})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "401", description = "Unauthorised"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @GetMapping("/annotation-sets/{id}")
    public ResponseEntity<AnnotationSetDTO> getAnnotationSet(@PathVariable UUID id) {
        log.debug("REST request to get AnnotationSet : {}", id);
        Optional<AnnotationSetDTO> annotationSetDTO = annotationSetService.findOne(id);
        return ResponseUtil.wrapOrNoContent(annotationSetDTO);
    }

    /**
     * DELETE  /annotation-sets/:id : delete the "id" annotationSet.
     *
     * @param id the id of the annotationSetDTO to delete
     * @return the ResponseEntity with status "200" (OK)
     */
    @Operation(summary = "Delete an annotationSetDTO", description = "A DELETE request to delete an annotationSetDTO",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "authorization",
                            description = "Authorization (Idam Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                            description = "Service Authorization (S2S Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.PATH, name = "id",
                            description = "AnnotationSet Id", required = true,
                            schema = @Schema(type = "UUID"))})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorised"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @DeleteMapping("/annotation-sets/{id}")
    public ResponseEntity<Void> deleteAnnotationSet(@PathVariable UUID id) {
        log.debug("REST request to delete AnnotationSet : {}", id);
        annotationSetService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
