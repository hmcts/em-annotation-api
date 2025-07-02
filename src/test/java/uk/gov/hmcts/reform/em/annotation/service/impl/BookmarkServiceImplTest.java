package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.Bookmark;
import uk.gov.hmcts.reform.em.annotation.repository.BookmarkRepository;
import uk.gov.hmcts.reform.em.annotation.service.dto.BookmarkDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.BookmarkMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookmarkServiceImplTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private BookmarkMapper bookmarkMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private BookmarkServiceImpl bookmarkServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    void saveBookmarkWithNullCreatedBySetsCurrentUser() {
        BookmarkDTO bookmarkDTO = new BookmarkDTO();
        bookmarkDTO.setId(UUID.randomUUID());
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("testUser"));
        when(bookmarkMapper.toEntity(any(BookmarkDTO.class))).thenReturn(new Bookmark());
        when(bookmarkRepository.save(any())).thenReturn(new Bookmark());
        when(bookmarkMapper.toDto(any(Bookmark.class))).thenReturn(bookmarkDTO);

        BookmarkDTO result = bookmarkServiceImpl.save(bookmarkDTO);

        Assertions.assertEquals("testUser", result.getCreatedBy());
        verify(bookmarkRepository).save(any());
    }

    @Test
    @Transactional
    void saveBookmarkWithNoCurrentUserThrowsBadCredentialsException() {
        BookmarkDTO bookmarkDTO = new BookmarkDTO();
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.empty());
        Assertions.assertThrowsExactly(
                BadCredentialsException.class,
                () -> bookmarkServiceImpl.save(bookmarkDTO),
                "Bad credentials."
        );
    }

    @Test
    @Transactional
    void findAllByDocumentIdWithValidUserReturnsBookmarks() {
        UUID documentId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("testUser"));
        when(bookmarkRepository.findByDocumentIdAndCreatedBy(documentId, "testUser", pageable))
                .thenReturn(Page.empty());

        Page<BookmarkDTO> result = bookmarkServiceImpl.findAllByDocumentId(documentId, pageable);

        Assertions.assertNotNull(result);
        verify(bookmarkRepository).findByDocumentIdAndCreatedBy(documentId, "testUser", pageable);
    }

    @Test
    @Transactional
    void findAllByDocumentIdWithNoCurrentUserThrowsBadCredentialsException() {
        UUID documentId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(
                BadCredentialsException.class,
                () -> bookmarkServiceImpl.findAllByDocumentId(documentId, pageable),
                "Bad credentials."
        );
    }

    @Test
    @Transactional
    void deleteBookmarkByIdDeletesSuccessfully() {
        UUID bookmarkId = UUID.randomUUID();

        bookmarkServiceImpl.delete(bookmarkId);

        verify(bookmarkRepository).deleteById(bookmarkId);
    }

    @Test
    @Transactional
    void deleteAllBookmarksByIdsDeletesSuccessfully() {
        List<UUID> bookmarkIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        bookmarkServiceImpl.deleteAllById(bookmarkIds);

        verify(bookmarkRepository).deleteAllById(bookmarkIds);
    }
}
