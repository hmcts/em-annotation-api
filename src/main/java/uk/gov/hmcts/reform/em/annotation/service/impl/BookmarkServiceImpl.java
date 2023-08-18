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
import uk.gov.hmcts.reform.em.annotation.service.BookmarkService;
import uk.gov.hmcts.reform.em.annotation.service.dto.BookmarkDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.BookmarkMapper;

import java.util.List;
import java.util.Optional;
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

    /**
     * Save a bookmark.
     *
     * @param bookmarkDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public BookmarkDTO save(BookmarkDTO bookmarkDTO) {
        log.debug("Request to save Bookmark : {}", bookmarkDTO);
        if (bookmarkDTO.getCreatedBy() == null) {
            bookmarkDTO.setCreatedBy(
                securityUtils.getCurrentUserLogin()
                    .orElseThrow(() -> {
                        log.error(String.format("User not found for Bookmark with Id : %s ", bookmarkDTO.getId()));
                        return new BadCredentialsException("Bad credentials.");
                    })
            );
        }

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
    public Page<BookmarkDTO> findAllByDocumentId(UUID documentId, Pageable pageable) {
        Optional<String> user = securityUtils.getCurrentUserLogin();
        if (user.isPresent()) {
            return bookmarkRepository.findByDocumentIdAndCreatedBy(documentId, user.get(), pageable)
                    .map(bookmarkMapper::toDto);
        } else {
            log.error(String.format("User not found for Document with Id : %s ", documentId));
            throw new BadCredentialsException("Bad credentials.");
        }
    }

    /**
     * Delete the "id" bookmark.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(UUID id) {
        log.debug("Request to delete Bookmark : {}", id);
        bookmarkRepository.deleteById(id);
    }

    @Override
    public void deleteAllById(List<UUID> ids) {
        log.debug("Request to delete Bookmarks : {}", ids);
        bookmarkRepository.deleteAllById(ids);
    }
}
