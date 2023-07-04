package uk.gov.hmcts.reform.em.annotation.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.gov.hmcts.reform.em.annotation.service.dto.BookmarkDTO;

import java.util.List;
import java.util.UUID;

public interface BookmarkService {

    /**
     * Save a bookmark.
     *
     * @param bookmarkDTO the entity to save
     * @return the persisted entity
     */
    BookmarkDTO save(BookmarkDTO bookmarkDTO);

    /**
     * Find document by documentId.
     * @param pageable pageable
     * @param documentId documentId
     * @return Page
     */
    Page<BookmarkDTO> findAllByDocumentId(UUID documentId, Pageable pageable);

    /**
     * Delete the "id" bookmark.
     *
     * @param id the id of the entity
     */
    void delete(UUID id);

    /**
     * Delete list of bookmarks.
     *
     * @param ids of the entity
     */
    void deleteAllById(List<UUID> ids);
}
