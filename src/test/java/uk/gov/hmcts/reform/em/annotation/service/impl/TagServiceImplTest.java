package uk.gov.hmcts.reform.em.annotation.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.Tag;
import uk.gov.hmcts.reform.em.annotation.repository.TagRepository;
import uk.gov.hmcts.reform.em.annotation.rest.errors.ResourceNotFoundException;
import uk.gov.hmcts.reform.em.annotation.service.dto.TagDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.TagMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private TagServiceImpl tagService;

    private static final String USER = "testUser";
    private Tag tag;
    private TagDTO tagDTO;

    @BeforeEach
    void setUp() {
        tag = new Tag();
        tag.setCreatedBy(USER);
        tagDTO = new TagDTO();
        tagDTO.setCreatedBy(USER);
    }

    @Test
    void findsTagsForValidUser() {
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(USER));
        when(tagRepository.findTagByCreatedBy(USER)).thenReturn(List.of(tag));
        when(tagMapper.toDto(tag)).thenReturn(tagDTO);

        List<TagDTO> result = tagService.findTagByCreatedBy(USER);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tagRepository).findTagByCreatedBy(USER);
    }

    @Test
    void throwsExceptionOnUserMismatch() {
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(USER));

        assertThrows(ResourceNotFoundException.class, () ->
            tagService.findTagByCreatedBy("otherUser")
        );

        verify(tagRepository, never()).findTagByCreatedBy(anyString());
    }

    @Test
    void throwsExceptionWhenUnauthenticated() {
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () ->
            tagService.findTagByCreatedBy(USER)
        );

        verify(tagRepository, never()).findTagByCreatedBy(anyString());
    }

    @Test
    void persistsTagSuccessfully() {
        tagService.persistTag(tag);
        verify(tagRepository).saveAndFlush(tag);
    }
}