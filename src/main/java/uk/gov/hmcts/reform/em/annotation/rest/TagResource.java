package uk.gov.hmcts.reform.em.annotation.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.em.annotation.service.TagService;
import uk.gov.hmcts.reform.em.annotation.service.dto.TagDTO;

import java.util.List;

/**
 * REST controller for managing Tags.
 */
@RestController
@RequestMapping("/api")
public class TagResource {
    private final Logger log = LoggerFactory.getLogger(TagResource.class);

    private final TagService tagService;

    public TagResource(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * GET  /tags/:id : get the tags "createdBy" user.
     *
     * @param createdBy the id of the user whose tags to retrieve
     * @return the ResponseEntity with status "200" (OK) and with body the annotationDTO, or with status '404' (Not Found)
     */
    @Operation(summary = "Get list of tags created by user", description = "A GET request to retrieve a list of tags")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorised"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @GetMapping("/tags/{createdBy}")
    public ResponseEntity<List<TagDTO>> getTagsCreatedBy(@PathVariable String createdBy) {
        log.debug("REST request to get Tags for : {}", createdBy);
        List<TagDTO> tags = tagService.findTagByCreatedBy(createdBy);
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }
}
