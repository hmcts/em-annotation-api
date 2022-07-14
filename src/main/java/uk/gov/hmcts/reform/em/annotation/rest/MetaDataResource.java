package uk.gov.hmcts.reform.em.annotation.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.em.annotation.config.Constants;
import uk.gov.hmcts.reform.em.annotation.rest.util.HeaderUtil;
import uk.gov.hmcts.reform.em.annotation.service.MetadataService;
import uk.gov.hmcts.reform.em.annotation.service.dto.MetadataDto;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.UUID;

/**
 * REST controller for managing Metadata.
 */
@ConditionalOnProperty("endpoint-toggles.metadata")
@RestController
@RequestMapping("/api/metadata")
@RequiredArgsConstructor
@Tag(name = "Metadata Service", description = "Endpoint for managing Metadata.")
public class MetaDataResource {

    private final Logger log = LoggerFactory.getLogger(MetaDataResource.class);

    private static final String ENTITY_NAME = "metadata";

    private final MetadataService metadataService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(Constants.IS_ADMIN);
    }

    /**
     * POST  /metadata : Create a new metadata.
     *
     * @param metadataDto the metadataDto to create
     * @return the ResponseEntity with status "201" (Created) and with body the new metadataDto
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Operation(summary = "Create an metadataDto", description = "A POST request to create an metadataDto",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "authorization",
                            description = "Authorization (Idam Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                            description = "Service Authorization (S2S Bearer token)", required = true,
                            schema = @Schema(type = "string"))})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created"),
        @ApiResponse(responseCode = "400", description = "metadataDto not valid"),
        @ApiResponse(responseCode = "401", description = "Unauthorised"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @PostMapping("/")
    public ResponseEntity<MetadataDto> createMetaData(@Validated @RequestBody MetadataDto metadataDto) throws URISyntaxException {

        log.debug("REST request to save Metadata : {}", metadataDto);

        MetadataDto createdMetadataDto = metadataService.save(metadataDto);

        final URI uri = new URI("/api/metadata/" + createdMetadataDto.getDocumentId());

        return ResponseEntity.created(uri)
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, createdMetadataDto.getDocumentId().toString()))
                .body(createdMetadataDto);
    }

    /**
     * GET  /metadata/:documentId : get the metadata for a specific document.
     *
     * @return the ResponseEntity with status "200" (OK)
     */
    @Operation(summary = "Get the metadata for Document ID",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "authorization",
                            description = "Authorization (Idam Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                            description = "Service Authorization (S2S Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.PATH, name = "documentId",
                            description = "Document Id", required = true,
                            schema = @Schema(type = "UUID"))})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorised"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @GetMapping("/{documentId}")
    public ResponseEntity<MetadataDto> getMetadata(@PathVariable UUID documentId) {

        log.debug("REST request to get the metadata for the Document");
        MetadataDto metadataDto = metadataService.findByDocumentId(documentId);

        if (Objects.isNull(metadataDto)
                || Objects.isNull(metadataDto.getRotationAngle())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(metadataDto);
    }

}
