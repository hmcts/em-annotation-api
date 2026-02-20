package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.Bookmark;
import uk.gov.hmcts.reform.em.annotation.repository.BookmarkRepository;
import uk.gov.hmcts.reform.em.annotation.rest.errors.ResourceNotFoundException;
import uk.gov.hmcts.reform.em.annotation.service.dto.BookmarkDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.BookmarkMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceImplTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private BookmarkMapper bookmarkMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private BookmarkServiceImpl bookmarkService;

    private static final String CURRENT_USER = "testUser";
    private static final String OTHER_USER = "otherUser";

    @Test
    void testSaveCreateNewBookmarkSuccess() {
        BookmarkDTO bookmarkDTO = new BookmarkDTO();
        bookmarkDTO.setId(null);

        Bookmark bookmark = new Bookmark();
        bookmark.setCreatedBy(CURRENT_USER);

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(bookmarkMapper.toEntity(bookmarkDTO)).thenReturn(bookmark);
        when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(bookmark);
        when(bookmarkMapper.toDto(any(Bookmark.class))).thenReturn(bookmarkDTO);

        BookmarkDTO result = bookmarkService.save(bookmarkDTO);

        assertNotNull(result);
        assertEquals(CURRENT_USER, bookmarkDTO.getCreatedBy());
        verify(bookmarkRepository).save(any(Bookmark.class));
    }

    @Test
    void testSaveUpdateExistingBookmarkSuccess() {
        UUID id = UUID.randomUUID();
        BookmarkDTO bookmarkDTO = new BookmarkDTO();
        bookmarkDTO.setId(id);

        Bookmark existingBookmark = new Bookmark();
        existingBookmark.setId(id);
        existingBookmark.setCreatedBy(CURRENT_USER);

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(bookmarkRepository.findById(id)).thenReturn(Optional.of(existingBookmark));
        when(bookmarkMapper.toEntity(bookmarkDTO)).thenReturn(existingBookmark);
        when(bookmarkRepository.save(existingBookmark)).thenReturn(existingBookmark);
        when(bookmarkMapper.toDto(existingBookmark)).thenReturn(bookmarkDTO);

        BookmarkDTO result = bookmarkService.save(bookmarkDTO);

        assertNotNull(result);
        verify(bookmarkRepository).findById(id);
        verify(bookmarkRepository).save(any(Bookmark.class));
    }

    @Test
    void testSaveUpdateExistingBookmarkWithDifferentUser() {
        UUID id = UUID.randomUUID();
        BookmarkDTO bookmarkDTO = new BookmarkDTO();
        bookmarkDTO.setId(id);

        Bookmark existingBookmark = new Bookmark();
        existingBookmark.setId(id);
        existingBookmark.setCreatedBy(OTHER_USER);

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(bookmarkRepository.findById(id)).thenReturn(Optional.of(existingBookmark));

        assertThrows(ResourceNotFoundException.class, () -> bookmarkService.save(bookmarkDTO));

        verify(bookmarkRepository, never()).save(any(Bookmark.class));
    }

    @Test
    void testSaveThrowsBadCredentialsWhenNoUserLoggedIn() {
        BookmarkDTO bookmarkDTO = new BookmarkDTO();
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> bookmarkService.save(bookmarkDTO));
    }

    @Test
    void testDeleteSuccess() {
        UUID id = UUID.randomUUID();
        Bookmark bookmark = new Bookmark();
        bookmark.setId(id);
        bookmark.setCreatedBy(CURRENT_USER);

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(bookmarkRepository.findById(id)).thenReturn(Optional.of(bookmark));

        bookmarkService.delete(id);

        verify(bookmarkRepository).deleteById(id);
    }

    @Test
    void testDeleteWithDifferentUser() {
        UUID id = UUID.randomUUID();
        Bookmark bookmark = new Bookmark();
        bookmark.setId(id);
        bookmark.setCreatedBy(OTHER_USER);

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(bookmarkRepository.findById(id)).thenReturn(Optional.of(bookmark));

        assertThrows(ResourceNotFoundException.class, () -> bookmarkService.delete(id));

        verify(bookmarkRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteNonExistentBookmarkDoesNothing() {
        UUID id = UUID.randomUUID();
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(bookmarkRepository.findById(id)).thenReturn(Optional.empty());

        bookmarkService.delete(id);

        verify(bookmarkRepository, never()).deleteById(id);
    }

    @Test
    void testDeleteAllByIdSuccess() {
        Bookmark b1 = new Bookmark();
        b1.setCreatedBy(CURRENT_USER);
        Bookmark b2 = new Bookmark();
        b2.setCreatedBy(CURRENT_USER);

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<UUID> ids = Arrays.asList(id1, id2);

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(bookmarkRepository.findAllById(ids)).thenReturn(Arrays.asList(b1, b2));

        bookmarkService.deleteAllById(ids);

        verify(bookmarkRepository).deleteAllById(ids);
    }

    @Test
    void testDeleteAllByIdForbidden() {

        Bookmark b1 = new Bookmark();
        b1.setCreatedBy(CURRENT_USER);
        Bookmark b2 = new Bookmark();
        b2.setCreatedBy(OTHER_USER);

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<UUID> ids = Arrays.asList(id1, id2);

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(bookmarkRepository.findAllById(ids)).thenReturn(Arrays.asList(b1, b2));

        assertThrows(ResourceNotFoundException.class, () -> bookmarkService.deleteAllById(ids));

        verify(bookmarkRepository, never()).deleteAllById(any());
    }

    @Test
    void testFindAllByDocumentIdSuccess() {
        UUID documentId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        Bookmark bookmark = new Bookmark();
        bookmark.setCreatedBy(CURRENT_USER);
        Page<Bookmark> page = new PageImpl<>(List.of(bookmark));

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(bookmarkRepository.findByDocumentIdAndCreatedBy(documentId, CURRENT_USER, pageable))
            .thenReturn(page);
        when(bookmarkMapper.toDto(bookmark)).thenReturn(new BookmarkDTO());

        Page<BookmarkDTO> result = bookmarkService.findAllByDocumentId(documentId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(bookmarkRepository).findByDocumentIdAndCreatedBy(documentId, CURRENT_USER, pageable);
    }
}