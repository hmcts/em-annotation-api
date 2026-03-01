package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.Bookmark;
import uk.gov.hmcts.reform.em.annotation.repository.BookmarkRepository;
import uk.gov.hmcts.reform.em.annotation.rest.errors.ResourceNotFoundException;
import uk.gov.hmcts.reform.em.annotation.service.BookmarkService;
import uk.gov.hmcts.reform.em.annotation.service.dto.BookmarkDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.BookmarkMapper;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Service Implementation for managing Bookmarks.
 */
@Service
@Transactional
public class BookmarkServiceImpl implements BookmarkService {

    private final Logger log = LoggerFactory.getLogger(BookmarkServiceImpl.class);

    private final BookmarkRepository bookmarkRepository;
    private final BookmarkMapper bookmarkMapper;
    private final SecurityUtils securityUtils;

    public BookmarkServiceImpl(BookmarkRepository bookmarkRepository,
                               BookmarkMapper bookmarkMapper,
                               SecurityUtils securityUtils) {
        this.bookmarkRepository = bookmarkRepository;
        this.bookmarkMapper = bookmarkMapper;
        this.securityUtils = securityUtils;
    }

    private String getCurrentUser() {
        return securityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadCredentialsException("User not found in security context."));
    }

    /**
     * Save a bookmark.
     *
     * @param bookmarkDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public BookmarkDTO save(BookmarkDTO bookmarkDTO) {
        bookmarkDTO.setCreatedBy(getCurrentUser());
        Bookmark bookmark = bookmarkMapper.toEntity(bookmarkDTO);
        bookmark = bookmarkRepository.save(bookmark);
        return bookmarkMapper.toDto(bookmark);
    }


    /**
     * Update a bookmark.
     *
     * @param bookmarkDTO the entity to update
     * @return the persisted entity
     */
    @Override
    public BookmarkDTO update(BookmarkDTO bookmarkDTO) {
        String currentUser = getCurrentUser();
        if (Objects.nonNull(bookmarkDTO.getId())) {
            bookmarkRepository.findById(bookmarkDTO.getId()).ifPresent(existingBookmark -> {
                if (!existingBookmark.getCreatedBy().equals(currentUser)) {
                    throw new ResourceNotFoundException("Bookmark not found");
                }
            });
        }
        bookmarkDTO.setCreatedBy(currentUser);
        Bookmark bookmark = bookmarkMapper.toEntity(bookmarkDTO);
        bookmark = bookmarkRepository.save(bookmark);
        return bookmarkMapper.toDto(bookmark);
    }

    /**
     * Find all document by Id.
     *
     * @param pageable pageable
     * @param documentId given documentId
     * @return bookmark Repository
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BookmarkDTO> findAllByDocumentId(UUID documentId, Pageable pageable) {
        String currentUser = getCurrentUser();
        return bookmarkRepository.findByDocumentIdAndCreatedBy(documentId, currentUser, pageable)
            .map(bookmarkMapper::toDto);
    }

    /**
     * Delete the "id" bookmark.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(UUID id) {
        String currentUser = getCurrentUser();
        log.debug("Request to delete Bookmark : {} by user {}", id, currentUser);

        bookmarkRepository.findById(id).ifPresent(bookmark -> {
            if (!bookmark.getCreatedBy().equals(currentUser)) {
                throw new ResourceNotFoundException("Bookmark not found");
            }
            bookmarkRepository.deleteById(id);
        });
    }

    @Override
    public void deleteAllById(List<UUID> ids) {
        String currentUser = getCurrentUser();
        log.debug("Request to delete Bookmarks : {} by user {}", ids, currentUser);
        long unauthorizedCount = bookmarkRepository.countByIdInAndCreatedByNot(ids, currentUser);
        if (unauthorizedCount > 0) {
            throw new ResourceNotFoundException("One or more bookmarks not found");
        }
        // Perform deletion in DB to avoid loading entities into memory.
        bookmarkRepository.deleteAllById(ids);

    }
}