package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.domain.Tag;
import uk.gov.hmcts.reform.em.annotation.repository.TagRepository;
import uk.gov.hmcts.reform.em.annotation.service.TagService;
import uk.gov.hmcts.reform.em.annotation.service.dto.TagDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.TagMapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Tag.
 */
@Service
@Transactional
public class TagServiceImpl implements TagService {

    private final Logger log = LoggerFactory.getLogger(TagServiceImpl.class);

    private final TagRepository tagRepository;

    private final TagMapper tagMapper;

    public TagServiceImpl(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    /**
     * Get all tags belonging to a particular user.
     *
     * @param createdBy the user who owns the tags
     * @return a list of tags belonging to the required user
     */
    @Override
    public List<TagDTO> findTagByCreatedBy(String createdBy) {
        return tagRepository.findTagsByCreatedBy(createdBy)
                .stream()
                .filter(t -> t.getAnnotation() == null)
                .filter(distinctByKey(Tag::getName))
                .map(tagMapper::toDto)
                .map(tag -> {
                    tag.setId(null);
                    return tag;
                })
                .collect(Collectors.toList());
    }

    /**
     * Method used to store tags that are to be kept separate from a particular annotation
     *
     * @param tag the new tag to be persisted
     */
    @Override
    public void persistTag(Tag tag) {
        tag.setId(UUID.randomUUID());
        tagRepository.saveAndFlush(tag);
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
