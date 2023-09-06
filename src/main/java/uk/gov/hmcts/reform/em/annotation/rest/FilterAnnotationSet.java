package uk.gov.hmcts.reform.em.annotation.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.em.annotation.rest.errors.EmptyResponseException;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationSetService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationSetDTO;

import java.util.Optional;

/**
 * REST controller for managing AnnotationSet.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "AnnotationSet Filter Service", description = "Endpoint for filtering AnnotationSet.")
public class FilterAnnotationSet {

    private final Logger log = LoggerFactory.getLogger(FilterAnnotationSet.class);

    private final AnnotationSetService annotationSetService;

    public FilterAnnotationSet(AnnotationSetService annotationSetService) {
        this.annotationSetService = annotationSetService;
    }

    @Operation(summary = "Filter an annotationSet", description = "A GET request to filter an annotationSetDTO",
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
    @GetMapping("/annotation-sets/filter")
    public ResponseEntity<AnnotationSetDTO> getAllAnnotationSets(@RequestParam("documentId") String documentId) {
        log.debug("REST request to get a page of AnnotationSets");

        Optional<AnnotationSetDTO> optionalAnnotationSetDTO = Optional.empty();
        try {
            optionalAnnotationSetDTO = annotationSetService.findOneByDocumentId(documentId);
        } catch (ConstraintViolationException | DataIntegrityViolationException exception) {
            log.error("constraintViolation : {} for documentId : {} ",
                    exception.getMessage(), documentId);
            return ResponseEntity.badRequest().build();
        }

        return optionalAnnotationSetDTO
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new EmptyResponseException("Could not find annotation set for this document id#"
                    + documentId));
    }
}
