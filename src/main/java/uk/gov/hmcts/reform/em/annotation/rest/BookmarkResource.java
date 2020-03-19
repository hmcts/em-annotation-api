package uk.gov.hmcts.reform.em.annotation.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import uk.gov.hmcts.reform.em.annotation.service.BookmarkService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.BookmarkDTO;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing Bookmarks.
 */
@RestController
@RequestMapping("/api")
public class BookmarkResource {

    private final Logger log = LoggerFactory.getLogger(BookmarkResource.class);

    private static final String ENTITY_NAME = "bookmark";

    private final BookmarkService bookmarkService;

    public BookmarkResource(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    /**
     * POST  /annotations : Create a new bookmark.
     *
     * @param bookmarkDTO the bookmarkDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new annotationDTO, or with status 400 (Bad Request) if the annotation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @ApiOperation(value = "Create an bookmarkDTO", notes = "A POST request to create an bookmarkDTO")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created", response = BookmarkDTO.class),
            @ApiResponse(code = 400, message = "annotationDTO not valid, invalid id"),
            @ApiResponse(code = 401, message = "Unauthorised"),
            @ApiResponse(code = 403, message = "Forbidden"),
    })
    @PostMapping("/bookmarks")
    //@Timed
    public ResponseEntity<AnnotationDTO> createBookmark(@RequestBody BookmarkDTO bookmarkDTO) throws URISyntaxException {
        log.debug("REST request to save Bookmark : {}", bookmarkDTO);
        if (bookmarkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        bookmarkService.save(bookmarkDTO);

        final URI uri = new URI("/api/bookmarks/" + bookmarkDTO.getId());
        return bookmarkService.findOne(bookmarkDTO.getId(), true).map( renderedAnnotation ->
                ResponseEntity.created(uri)
                        .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, bookmarkDTO.getId().toString()))
                        .body(renderedAnnotation)
        )
                .orElse(ResponseEntity.badRequest().build());
    }

    /**
     * GET  /bookmarks/:documentId : get all the bookmarks for a specific document.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of bookmarks in body
     */
    @ApiOperation(value = "Get all bookmarks for Document ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = BookmarkDTO.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "Unauthorised"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
    })
    @GetMapping("/bookmarks/{documentId}")
    //@Timed
    public ResponseEntity<List<BookmarkDTO>> getAllDocumentBookmarks(@PathVariable UUID documentId, Pageable pageable) {
        // need to receive the user Id in the requestbody or somewhere similar.

        log.debug("REST request to get a page of Bookmarks");
        Page<BookmarkDTO> page = bookmarkService.findAllDocumentBookmarks(documentId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/annotations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    // update bookmark (need bookmarkId)
    // need to verify the update is being made by user that created the bookmark

    /**
     * DELETE  /bookmarks/:id : delete the "id" bookmark.
     *
     * @param id the id of the bookmarkDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @ApiOperation(value = "Delete a BookmarkDTO", notes = "A DELETE request to delete a BookmarkDTO")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorised"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
    })
    @DeleteMapping("/bookmarks/{id}")
    //@Timed
    public ResponseEntity<Void> deleteBookmark(@PathVariable UUID id) {
        log.debug("REST request to delete Bookmark : {}", id);
        bookmarkService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
