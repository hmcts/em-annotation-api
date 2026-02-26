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
    void testSaveSuccess() {
        BookmarkDTO bookmarkDTO = new BookmarkDTO();
        Bookmark bookmark = new Bookmark();

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
    void testUpdateSuccess() {
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

        BookmarkDTO result = bookmarkService.update(bookmarkDTO);

        assertNotNull(result);
        assertEquals(CURRENT_USER, bookmarkDTO.getCreatedBy());
        verify(bookmarkRepository).save(existingBookmark);
    }

    @Test
    void testUpdateForbidden() {
        UUID id = UUID.randomUUID();
        BookmarkDTO bookmarkDTO = new BookmarkDTO();
        bookmarkDTO.setId(id);

        Bookmark existingBookmark = new Bookmark();
        existingBookmark.setId(id);
        existingBookmark.setCreatedBy(OTHER_USER);

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(bookmarkRepository.findById(id)).thenReturn(Optional.of(existingBookmark));

        assertThrows(ResourceNotFoundException.class, () -> bookmarkService.update(bookmarkDTO));
        verify(bookmarkRepository, never()).save(any());
    }

    @Test
    void testFindAllByDocumentIdSuccess() {
        UUID documentId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        Bookmark bookmark = new Bookmark();
        Page<Bookmark> page = new PageImpl<>(List.of(bookmark));

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(bookmarkRepository.findByDocumentIdAndCreatedBy(documentId, CURRENT_USER, pageable))
            .thenReturn(page);
        when(bookmarkMapper.toDto(bookmark)).thenReturn(new BookmarkDTO());

        Page<BookmarkDTO> result = bookmarkService.findAllByDocumentId(documentId, pageable);

        assertNotNull(result);
        verify(bookmarkRepository).findByDocumentIdAndCreatedBy(documentId, CURRENT_USER, pageable);
    }

    @Test
    void testDeleteSuccess() {
        UUID id = UUID.randomUUID();
        Bookmark bookmark = new Bookmark();
        bookmark.setCreatedBy(CURRENT_USER);

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(bookmarkRepository.findById(id)).thenReturn(Optional.of(bookmark));

        bookmarkService.delete(id);

        verify(bookmarkRepository).deleteById(id);
    }

    @Test
    void testDeleteForbidden() {
        UUID id = UUID.randomUUID();
        Bookmark bookmark = new Bookmark();
        bookmark.setCreatedBy(OTHER_USER);

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(bookmarkRepository.findById(id)).thenReturn(Optional.of(bookmark));

        assertThrows(ResourceNotFoundException.class, () -> bookmarkService.delete(id));
        verify(bookmarkRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteAllByIdSuccess() {
        List<UUID> ids = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        when(bookmarkRepository.countByIdInAndCreatedByNot(ids, CURRENT_USER)).thenReturn(0L);

        bookmarkService.deleteAllById(ids);

        verify(bookmarkRepository).deleteAllById(ids);
    }

    @Test
    void testDeleteAllByIdForbidden() {
        List<UUID> ids = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(CURRENT_USER));
        // Mock that 1 bookmark in the list belongs to someone else
        when(bookmarkRepository.countByIdInAndCreatedByNot(ids, CURRENT_USER)).thenReturn(1L);

        assertThrows(ResourceNotFoundException.class, () -> bookmarkService.deleteAllById(ids));
        verify(bookmarkRepository, never()).deleteAllById(any());
    }

    @Test
    void testGetCurrentUserThrowsException() {
        BookmarkDTO dto = new BookmarkDTO();
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> bookmarkService.save(dto));
    }
}