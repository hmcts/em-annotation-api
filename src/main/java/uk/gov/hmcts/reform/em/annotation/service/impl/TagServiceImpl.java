package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.Tag;
import uk.gov.hmcts.reform.em.annotation.repository.TagRepository;
import uk.gov.hmcts.reform.em.annotation.rest.errors.ResourceNotFoundException;
import uk.gov.hmcts.reform.em.annotation.service.TagService;
import uk.gov.hmcts.reform.em.annotation.service.dto.TagDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.TagMapper;

import java.util.List;

/**
 * Service Implementation for managing Tag.
 */
@Service
@Transactional
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final SecurityUtils securityUtils;

    public TagServiceImpl(TagRepository tagRepository,
                          TagMapper tagMapper,
                          SecurityUtils securityUtils) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
        this.securityUtils = securityUtils;
    }

    private String getCurrentUser() {
        return securityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new BadCredentialsException("User not found in security context."));
    }

    /**
     * Get all tags belonging to a particular user.
     *
     * @param createdBy the user who owns the tags
     * @return a list of tags belonging to the required user
     */
    @Override
    public List<TagDTO> findTagByCreatedBy(String createdBy) {
        String currentUser = getCurrentUser();

        if (!currentUser.equals(createdBy)) {
            throw new ResourceNotFoundException("Tags not found");
        }

        return tagRepository.findTagByCreatedBy(currentUser)
            .stream()
            .map(tagMapper::toDto)
            .toList();
    }

    /**
     * Persist tag entity in the database.
     *
     * @param tag the tag to be persisted to the database
     */
    @Override
    public void persistTag(Tag tag) {
        tagRepository.saveAndFlush(tag);
    }
}
