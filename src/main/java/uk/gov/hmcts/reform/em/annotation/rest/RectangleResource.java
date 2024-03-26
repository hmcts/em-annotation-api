package uk.gov.hmcts.reform.em.annotation.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import uk.gov.hmcts.reform.em.annotation.service.AnnotationService;
import uk.gov.hmcts.reform.em.annotation.service.RectangleService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.RectangleDTO;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing Rectangle.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Rectangle Service", description = "Endpoint for managing Rectangles.")
public class RectangleResource {

    private final Logger log = LoggerFactory.getLogger(RectangleResource.class);

    private static final String ENTITY_NAME = "rectangle";
    private static final String INVALID_ID = "Invalid id";
    private static final String NULL_ENTITY = "idnull";

    private final RectangleService rectangleService;

    private final AnnotationService annotationService;

    @Autowired
    public RectangleResource(AnnotationService annotationService, RectangleService rectangleService) {
        this.annotationService = annotationService;
        this.rectangleService = rectangleService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(Constants.IS_ADMIN);
    }

    /**
     * POST  /rectangles : Create a new rectangle.
     *
     * @param rectangleDTO the rectangleDTO to create
     * @return the ResponseEntity with status "201" (Created) and with body the new rectangleDTO, or
     *      with status "400" (Bad Request) if the rectangle has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Operation(summary = "Create a rectangleDTO", description = "A POST request to create a rectangleDTO",
            parameters = {
                @Parameter(in = ParameterIn.HEADER, name = "authorization",
                        description = "Authorization (Idam Bearer token)", required = true,
                        schema = @Schema(type = "string")),
                @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                        description = "Service Authorization (S2S Bearer token)", required = true,
                        schema = @Schema(type = "string"))})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created"),
        @ApiResponse(responseCode = "400", description = "rectangleDTO not valid, invalid id"),
        @ApiResponse(responseCode = "401", description = "Unauthorised"),
        @ApiResponse(responseCode = "404", description = "Annotation Id not Found"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @PostMapping("/rectangles")
    public ResponseEntity<RectangleDTO> createRectangle(@RequestBody RectangleDTO rectangleDTO)
        throws URISyntaxException {
        log.debug("REST request to save Rectangle : {}", rectangleDTO);
        if (Objects.isNull(rectangleDTO.getId())) {
            throw new BadRequestAlertException(INVALID_ID, ENTITY_NAME, NULL_ENTITY);
        }
        if (Objects.isNull(rectangleDTO.getAnnotationId())) {
            throw new BadRequestAlertException("Invalid Annotation id", ENTITY_NAME, NULL_ENTITY);
        }
        Optional<AnnotationDTO> annotationDTOOptional
            = annotationService.findOne(rectangleDTO.getAnnotationId());
        if (annotationDTOOptional.isEmpty() || Objects.isNull(annotationDTOOptional.get().getId())) {
            return ResponseEntity
                .notFound()
                .build();
        }
        RectangleDTO result = rectangleService.save(rectangleDTO);
        return ResponseEntity.created(new URI("/api/rectangles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /rectangles : Updates an existing rectangle.
     *
     * @param rectangleDTO the rectangleDTO to update
     * @return the ResponseEntity with status "200" (OK) and with body the updated rectangleDTO, or
     *      with status "400" (Bad Request) if the rectangleDTO is not valid, or with status "500"
     *      (Internal Server Error) if the rectangleDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Operation(summary = "Update an existing rectangleDTO", description = "A PUT request to update a rectangleDTO",
            parameters = {
                @Parameter(in = ParameterIn.HEADER, name = "authorization",
                        description = "Authorization (Idam Bearer token)", required = true,
                        schema = @Schema(type = "string")),
                @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                        description = "Service Authorization (S2S Bearer token)", required = true,
                        schema = @Schema(type = "string"))})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "rectangleDTO not valid, invalid id"),
        @ApiResponse(responseCode = "500", description = "rectangleDTO couldn't be updated"),
        @ApiResponse(responseCode = "401", description = "Unauthorised"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @PutMapping("/rectangles")
    public ResponseEntity<RectangleDTO> updateRectangle(@RequestBody RectangleDTO rectangleDTO)
        throws URISyntaxException {
        log.debug("REST request to update Rectangle : {}", rectangleDTO);
        if (rectangleDTO.getId() == null) {
            throw new BadRequestAlertException(INVALID_ID, ENTITY_NAME, NULL_ENTITY);
        }
        RectangleDTO result = rectangleService.save(rectangleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, rectangleDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /rectangles : get all the rectangles.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status "200" (OK) and the list of rectangles in body
     */
    @Operation(summary = "Get all comments", description = "A GET request without a body is used to retrieve "
            + "all comments",
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
    @GetMapping("/rectangles")
    public ResponseEntity<List<RectangleDTO>> getAllRectangles(Pageable pageable) {
        log.debug("REST request to get a page of Rectangles");
        Page<RectangleDTO> page = rectangleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/rectangles");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /rectangles/:id : get the "id" rectangle.
     *
     * @param id the id of the rectangleDTO to retrieve
     * @return the ResponseEntity with status "200" (OK) and with body the rectangleDTO,
     *      or with status "404" (Not Found)
     */
    @Operation(summary = "Get an existing rectangleDTO", description = "A GET request to retrieve a rectangleDTO",
            parameters = {
                @Parameter(in = ParameterIn.HEADER, name = "authorization",
                        description = "Authorization (Idam Bearer token)", required = true,
                        schema = @Schema(type = "string")),
                @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                        description = "Service Authorization (S2S Bearer token)", required = true,
                        schema = @Schema(type = "string")),
                @Parameter(in = ParameterIn.PATH, name = "id",
                        description = "Rectangle Id", required = true,
                        schema = @Schema(type = "UUID"))})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorised"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @GetMapping("/rectangles/{id}")
    public ResponseEntity<RectangleDTO> getRectangle(@PathVariable UUID id) {
        log.debug("REST request to get Rectangle : {}", id);
        Optional<RectangleDTO> rectangleDTO = rectangleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rectangleDTO);
    }

    /**
     * DELETE  /rectangles/:id : delete the "id" rectangle.
     *
     * @param id the id of the rectangleDTO to delete
     * @return the ResponseEntity with status "200" (OK)
     */
    @Operation(summary = "Delete a commentDTO", description = "A DELETE request to delete a commentDTO",
            parameters = {
                @Parameter(in = ParameterIn.HEADER, name = "authorization",
                        description = "Authorization (Idam Bearer token)", required = true,
                        schema = @Schema(type = "string")),
                @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                        description = "Service Authorization (S2S Bearer token)", required = true,
                        schema = @Schema(type = "string")),
                @Parameter(in = ParameterIn.PATH, name = "id",
                        description = "Rectangle Id", required = true,
                        schema = @Schema(type = "UUID"))})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorised"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @DeleteMapping("/rectangles/{id}")
    public ResponseEntity<Void> deleteRectangle(@PathVariable UUID id) {
        log.debug("REST request to delete Rectangle : {}", id);
        rectangleService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString()))
            .build();
    }
}
