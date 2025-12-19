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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.em.annotation.service.DocumentDataService;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Document Data", description = "Endpoints to manage data related to documents")
public class DocumentDataResource {

    private final Logger log = LoggerFactory.getLogger(DocumentDataResource.class);
    private final DocumentDataService documentDataService;

    public DocumentDataResource(DocumentDataService documentDataService) {
        this.documentDataService = documentDataService;
    }

    @Operation(summary = "Delete all data related to a document",
        description = "A DELETE request to remove all annotations, bookmarks, "
            + "and metadata associated with a specific document ID. This operation is idempotent.",
        parameters = {
            @Parameter(in = ParameterIn.HEADER, name = "authorization",
                description = "Authorization (Idam Bearer token)", required = true,
                schema = @Schema(type = "string")),
            @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                description = "Service Authorization (S2S Bearer token)", required = true,
                schema = @Schema(type = "string")),
            @Parameter(in = ParameterIn.PATH, name = "docId",
                description = "The UUID of the Document to be purged", required = true,
                schema = @Schema(type = "string", format = "uuid"))
        })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Data deleted successfully or did not exist"),
        @ApiResponse(responseCode = "400", description = "Invalid Document ID format"),
        @ApiResponse(responseCode = "401", description = "Unauthorised (Missing or invalid tokens)"),
        @ApiResponse(responseCode = "403",
            description = "Forbidden - Calling service is not whitelisted to perform this operation"),
    })
    @DeleteMapping("/documents/{docId}/data")
    @ConditionalOnProperty("endpoint-toggles.document-data-deletion")
    public ResponseEntity<Void> deleteDocumentData(@PathVariable UUID docId) {
        log.debug("REST request to delete data for document: {}", docId);
        documentDataService.deleteDocumentData(docId);
        return ResponseEntity.noContent().build();
    }
}