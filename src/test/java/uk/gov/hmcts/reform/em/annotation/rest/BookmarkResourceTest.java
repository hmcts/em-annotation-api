package uk.gov.hmcts.reform.em.annotation.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.em.annotation.rest.errors.BadRequestAlertException;
import uk.gov.hmcts.reform.em.annotation.service.BookmarkService;
import uk.gov.hmcts.reform.em.annotation.service.dto.BookmarkDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.DeleteBookmarkDTO;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookmarkResourceTest {

    @Mock
    private BookmarkService bookmarkService;

    @InjectMocks
    private BookmarkResource bookmarkResource;

    private BookmarkDTO bookmarkDTO;
    private UUID bookmarkId;
    private UUID documentId;

    private static final String ENTITY_CREATION_ALERT = "X-emannotationapp-alert";
    private static final String ENTITY_UPDATE_ALERT = "X-emannotationapp-alert";
    private static final String ENTITY_DELETION_ALERT = "X-emannotationapp-alert";

    @BeforeEach
    void setUp() {
        bookmarkId = UUID.randomUUID();
        documentId = UUID.randomUUID();
        bookmarkDTO = new BookmarkDTO();
        bookmarkDTO.setId(bookmarkId);
        bookmarkDTO.setDocumentId(documentId);
        bookmarkDTO.setPageNumber(1);
    }

    @Test
    void createBookmarkSuccess() throws URISyntaxException {
        when(bookmarkService.save(any(BookmarkDTO.class))).thenReturn(bookmarkDTO);

        ResponseEntity<BookmarkDTO> response = bookmarkResource.createBookmark(bookmarkDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(bookmarkDTO);
        assertThat(response.getHeaders().getFirst(ENTITY_CREATION_ALERT)).isNotNull();
        assertThat(response.getHeaders().getLocation()).hasPath("/api/bookmarks/" + bookmarkId);

        verify(bookmarkService).save(bookmarkDTO);
    }

    @Test
    void createBookmarkThrowsBadRequestWhenIdIsNull() {
        bookmarkDTO.setId(null);

        assertThatThrownBy(() -> bookmarkResource.createBookmark(bookmarkDTO))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessage("Invalid id");
    }

    @Test
    void createBookmarkThrowsBadRequestWhenPageNumberIsNegative() {
        bookmarkDTO.setPageNumber(-1);

        assertThatThrownBy(() -> bookmarkResource.createBookmark(bookmarkDTO))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessage("Page number must not be negative or null");
    }

    @Test
    void createBookmarkThrowsBadRequestWhenPageNumberIsNull() {
        bookmarkDTO.setPageNumber(null);

        assertThatThrownBy(() -> bookmarkResource.createBookmark(bookmarkDTO))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessage("Page number must not be negative or null");
    }

    @Test
    void updateBookmarkSuccess() {
        when(bookmarkService.update(any(BookmarkDTO.class))).thenReturn(bookmarkDTO);

        ResponseEntity<BookmarkDTO> response = bookmarkResource.updateBookmark(bookmarkDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(bookmarkDTO);
        assertThat(response.getHeaders().getFirst(ENTITY_UPDATE_ALERT)).isNotNull();

        verify(bookmarkService).update(bookmarkDTO);
    }

    @Test
    void updateBookmarkThrowsBadRequestWhenIdIsNull() {
        bookmarkDTO.setId(null);

        assertThatThrownBy(() -> bookmarkResource.updateBookmark(bookmarkDTO))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessage("Invalid id");
    }

    @Test
    void updateMultipleBookmarksSuccess() {
        List<BookmarkDTO> bookmarks = List.of(bookmarkDTO);
        when(bookmarkService.update(any(BookmarkDTO.class))).thenReturn(bookmarkDTO);

        ResponseEntity<List<BookmarkDTO>> response = bookmarkResource.updateMultipleBookmarks(bookmarks);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst()).isEqualTo(bookmarkDTO);
        assertThat(response.getHeaders().getFirst(ENTITY_UPDATE_ALERT)).isNotNull();

        verify(bookmarkService).update(bookmarkDTO);
    }

    @Test
    void updateMultipleBookmarksThrowsBadRequestWhenAnyIdIsNull() {
        BookmarkDTO validBookmark = new BookmarkDTO();
        validBookmark.setId(UUID.randomUUID());

        BookmarkDTO invalidBookmark = new BookmarkDTO();
        invalidBookmark.setId(null);

        List<BookmarkDTO> bookmarks = List.of(validBookmark, invalidBookmark);

        assertThatThrownBy(() -> bookmarkResource.updateMultipleBookmarks(bookmarks))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessage("Invalid id");
    }

    @Test
    void getAllDocumentBookmarksSuccess() {
        Page<BookmarkDTO> page = new PageImpl<>(List.of(bookmarkDTO));
        when(bookmarkService.findAllByDocumentId(any(UUID.class), any(Pageable.class))).thenReturn(page);

        ResponseEntity<List<BookmarkDTO>> response = bookmarkResource.getAllDocumentBookmarks(documentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(bookmarkDTO);
        assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("1");
    }

    @Test
    void getAllDocumentBookmarksReturnsNoContentWhenEmpty() {
        Page<BookmarkDTO> page = new PageImpl<>(Collections.emptyList());
        when(bookmarkService.findAllByDocumentId(any(UUID.class), any(Pageable.class))).thenReturn(page);

        ResponseEntity<List<BookmarkDTO>> response = bookmarkResource.getAllDocumentBookmarks(documentId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void deleteBookmarkSuccess() {
        ResponseEntity<Void> response = bookmarkResource.deleteBookmark(bookmarkId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(ENTITY_DELETION_ALERT)).isNotNull();

        verify(bookmarkService).delete(bookmarkId);
    }

    @Test
    void deleteMultipleBookmarksSuccessWithUpdatedEntity() {
        DeleteBookmarkDTO deleteDTO = new DeleteBookmarkDTO();
        deleteDTO.setDeleted(List.of(bookmarkId));
        deleteDTO.setUpdated(bookmarkDTO);

        ResponseEntity<Void> response = bookmarkResource.deleteMultipleBookmarks(deleteDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(ENTITY_DELETION_ALERT)).isNotNull();

        verify(bookmarkService).deleteAllById(deleteDTO.getDeleted());
        verify(bookmarkService).save(bookmarkDTO);
    }

    @Test
    void deleteMultipleBookmarksSuccessWithoutUpdatedEntity() {
        DeleteBookmarkDTO deleteDTO = new DeleteBookmarkDTO();
        deleteDTO.setDeleted(List.of(bookmarkId));
        deleteDTO.setUpdated(null);

        ResponseEntity<Void> response = bookmarkResource.deleteMultipleBookmarks(deleteDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(ENTITY_DELETION_ALERT)).isNotNull();

        verify(bookmarkService).deleteAllById(deleteDTO.getDeleted());
        verify(bookmarkService, never()).save(any(BookmarkDTO.class));
    }

    @Test
    void deleteMultipleBookmarksThrowsBadRequestWhenAnyDeletedIdIsNull() {
        DeleteBookmarkDTO deleteDTO = new DeleteBookmarkDTO();
        List<UUID> ids = new ArrayList<>();
        ids.add(bookmarkId);
        ids.add(null);
        deleteDTO.setDeleted(ids);

        assertThatThrownBy(() -> bookmarkResource.deleteMultipleBookmarks(deleteDTO))
            .isInstanceOf(BadRequestAlertException.class)
            .hasMessage("Invalid id");
    }
}