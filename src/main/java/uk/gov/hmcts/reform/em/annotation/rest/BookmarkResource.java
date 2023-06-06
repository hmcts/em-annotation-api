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
import org.springframework.dao.EmptyResultDataAccessException;
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
import uk.gov.hmcts.reform.em.annotation.service.BookmarkService;
import uk.gov.hmcts.reform.em.annotation.service.dto.BookmarkDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.DeleteBookmarkDTO;
import uk.gov.hmcts.reform.em.annotation.service.util.StringUtilities;

import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for managing Bookmarks.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Bookmark Service", description = "Endpoint for managing Bookmarks.")
public class BookmarkResource {

    private final Logger log = LoggerFactory.getLogger(BookmarkResource.class);

    private static final String ENTITY_NAME = "bookmark";
    private static final String INVALID_ID = "Invalid id";
    private static final String NULL_ENTITY = "idnull";

    private final BookmarkService bookmarkService;

    public BookmarkResource(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(Constants.IS_ADMIN);
    }

    /**
     * POST  /bookmarks : Create a new bookmark.
     *
     * @param bookmarkDTO the bookmarkDTO to create
     * @return the ResponseEntity with status "201" (Created) and with body the new bookmarkDTO, or with status "400" (Bad
     *      Request) if the bookmark has an invalid ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Operation(summary = "Create an bookmarkDTO", description = "A POST request to create an bookmarkDTO",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "authorization",
                            description = "Authorization (Idam Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                            description = "Service Authorization (S2S Bearer token)", required = true,
                            schema = @Schema(type = "string"))})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created"),
        @ApiResponse(responseCode = "400", description = "bookmarkDTO not valid, invalid id"),
        @ApiResponse(responseCode = "401", description = "Unauthorised"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @PostMapping("/bookmarks")
    public ResponseEntity<BookmarkDTO> createBookmark(@RequestBody BookmarkDTO bookmarkDTO) throws URISyntaxException {
        log.debug("REST request to save Bookmark : {}", bookmarkDTO);
        if (bookmarkDTO.getId() == null) {
            throw new BadRequestAlertException(INVALID_ID, ENTITY_NAME, NULL_ENTITY);
        }
        BookmarkDTO result = bookmarkService.save(bookmarkDTO);
        return ResponseEntity.created(new URI("/api/bookmarks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /bookmarks : Updates an existing bookmark.
     *
     * @param bookmarkDTO the bookmarkDTO to update
     * @return the ResponseEntity with status "200" (OK) and with body the updated bookmarkDTO, or with status "400" (Bad
     *     Request) if the bookmarkDTO is not valid, or with status "500" (Internal Server Error) if the bookmarkDTO couldn't
     *     be updated
     */
    @Operation(summary = "Update an existing bookmarkDTO", description = "A PUT request to update an bookmarkDTO",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "authorization",
                            description = "Authorization (Idam Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                            description = "Service Authorization (S2S Bearer token)", required = true,
                            schema = @Schema(type = "string"))})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "bookmarkDTO not valid, invalid id"),
        @ApiResponse(responseCode = "500", description = "bookmarkDTO couldn't be updated"),
        @ApiResponse(responseCode = "401", description = "Unauthorised"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @PutMapping("/bookmarks")
    public ResponseEntity<BookmarkDTO> updateBookmark(@Valid @RequestBody BookmarkDTO bookmarkDTO) {
        log.debug("REST request to update Bookmark : {}", bookmarkDTO);
        if (bookmarkDTO.getId() == null) {
            throw new BadRequestAlertException(INVALID_ID, ENTITY_NAME, NULL_ENTITY);
        }
        BookmarkDTO result = bookmarkService.save(bookmarkDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, bookmarkDTO.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /bookmarks_multiple : Updates multiple existing bookmarks.
     *
     * @param bookmarkDTOList the list of bookmarkDTO objects to update
     * @return the ResponseEntity with status "200" (OK) and with body the updated bookmarkDTO objects, or with status "400"
     *      (Bad Request) if the bookmarkDTO objects are not valid, or with status "500" (Internal Server Error) if the
     *      bookmarkDTO objects couldn't be updated.
     */
    @Operation(summary = "Update multiple existing bookmarkDTO objects",
            description = "A PUT request to update multiple bookmarkDTO objects",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "authorization",
                            description = "Authorization (Idam Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                            description = "Service Authorization (S2S Bearer token)", required = true,
                            schema = @Schema(type = "string"))})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "bookmarkDTO objects not valid, invalid id"),
        @ApiResponse(responseCode = "500", description = "bookmarkDTO objects couldn't be updated"),
        @ApiResponse(responseCode = "401", description = "Unauthorised"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @PutMapping("/bookmarks_multiple")
    public ResponseEntity<List<BookmarkDTO>> updateMultipleBookmarks(
        @Valid @RequestBody List<BookmarkDTO> bookmarkDTOList) {

        if (bookmarkDTOList.stream().anyMatch(bookmark -> bookmark.getId() == null)) {
            throw new BadRequestAlertException(INVALID_ID, ENTITY_NAME, NULL_ENTITY);
        }

        List<UUID> sanitisedList = StringUtilities.convertValidLogUUID(bookmarkDTOList.stream()
            .map(BookmarkDTO::getId)
            .collect(Collectors.toList()));

        log.debug("REST request to update list of Bookmark objects : {}", sanitisedList);

        List<BookmarkDTO> result = bookmarkDTOList.stream()
            .map(bookmarkService::save)
            .collect(Collectors.toList());

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME,sanitisedList.toString()))
            .body(result);
    }

    /**
     * GET  /bookmarks/:documentId : get all the bookmarks for a specific document.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status "200" (OK) and the list of bookmarks in body
     */
    @Operation(summary = "Get all bookmarks for Document ID",
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
    @GetMapping("/{documentId}/bookmarks")
    public ResponseEntity<List<BookmarkDTO>> getAllDocumentBookmarks(@PathVariable UUID documentId) {
        log.debug("REST request to get all Bookmarks");
        Pageable unpaged = Pageable.unpaged();
        Page<BookmarkDTO> page = bookmarkService.findAllByDocumentId(documentId, unpaged);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bookmarks");
        if (page.hasContent()) {
            return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE  /bookmarks/:id : delete the "id" bookmark.
     *
     * @param id the id of the bookmarkDTO to delete
     * @return the ResponseEntity with status "200" (OK)
     */
    @Operation(summary = "Delete a BookmarkDTO", description = "A DELETE request to delete a BookmarkDTO",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "authorization",
                            description = "Authorization (Idam Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "serviceauthorization",
                            description = "Service Authorization (S2S Bearer token)", required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.PATH, name = "id",
                            description = "Bookmark Id", required = true,
                            schema = @Schema(type = "UUID"))})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorised"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @DeleteMapping("/bookmarks/{id}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable UUID id) {
        log.debug("REST request to delete Bookmark : {}", id);
        try {
            bookmarkService.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString()))
                .build();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE  /bookmarks : delete multiple bookmarks.
     *
     * @param deleteBookmarkDTO object containing the list of bookmarkDTO objects to delete and parent to update
     * @return the ResponseEntity with status "200" (OK), or with status "400" (Bad Request) if the bookmarkDTO objects are
     *      not valid, or with status "500" (Internal Server Error) if the bookmarkDTO objects couldn't be deleted.
     */
    @Operation(summary = "Delete multiple existing bookmarkDTO objects",
            description = "A DELETE request to delete multiple bookmarkDTO objects",
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
    @DeleteMapping("/bookmarks_multiple")
    public ResponseEntity<Void> deleteMultipleBookmarks(@Valid @RequestBody DeleteBookmarkDTO deleteBookmarkDTO) {

        if (deleteBookmarkDTO.getDeleted().stream().anyMatch(Objects::isNull)) {
            throw new BadRequestAlertException(INVALID_ID, ENTITY_NAME, NULL_ENTITY);
        }

        List<UUID> sanitisedList = StringUtilities.convertValidLogUUID(deleteBookmarkDTO.getDeleted());

        log.debug("REST request to delete list of Bookmark objects : {}", sanitisedList);

        Optional<UUID> idToBeDeleted = Optional.empty();
        try {
            for (UUID id : deleteBookmarkDTO.getDeleted()) {
                idToBeDeleted = Optional.ofNullable(id);
                bookmarkService.delete(id);
            }
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            idToBeDeleted.ifPresent(uuid -> log.debug("The Delete ID is not Found : {}", uuid));
            return ResponseEntity
                .notFound()
                .build();
        }

        if (!Objects.isNull(deleteBookmarkDTO.getUpdated())) {
            bookmarkService.save(deleteBookmarkDTO.getUpdated());
        }

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityDeletionAlert(
                ENTITY_NAME, sanitisedList.toString()))
            .build();
    }
}
