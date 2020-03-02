package uk.gov.hmcts.reform.em.annotation.service;

import uk.gov.hmcts.reform.em.annotation.domain.Tag;
import uk.gov.hmcts.reform.em.annotation.service.dto.TagDTO;

import java.util.List;

/**
 * Service Interface for managing Tag.
 */
public interface TagService {

    /**
     * Get all tags belonging to a particular user.
     *
     * @param createdBy the user who owns the tags
     * @return a list of tags belonging to the required user
     */
    List<TagDTO> findTagByCreatedBy(String createdBy);

    /**
     * Method used to store tags that are to be kept separate from a particular annotation
     *
     * @param tag the new tag to be persisted
     */
    void persistTag(Tag tag);
}
