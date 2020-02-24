package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.repository.CommentTagRepository;
import uk.gov.hmcts.reform.em.annotation.service.CommentTagService;
import uk.gov.hmcts.reform.em.annotation.service.dto.CommentTagDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.CommentTagMapper;

/**
 * Service Implementation for managing Comment Tag.
 */
@Service
@Transactional
public class CommentTagServiceImpl implements CommentTagService {

    private final Logger log = LoggerFactory.getLogger(CommentTagServiceImpl.class);

    private final CommentTagRepository commentTagRepository;

    private final CommentTagMapper commentTagMapper;

    public CommentTagServiceImpl(CommentTagRepository commentTagRepository, CommentTagMapper commentTagMapper) {
        this.commentTagRepository = commentTagRepository;
        this.commentTagMapper = commentTagMapper;
    }

    /**
     * Get all the comment tags.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CommentTagDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Comment Tags");
        return commentTagRepository.findAll(pageable)
                .map(commentTagMapper::toDto);
    }
}
