package uk.gov.hmcts.reform.em.annotation.rest;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.BaseTest;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.domain.Tag;
import uk.gov.hmcts.reform.em.annotation.repository.TagRepository;

import java.util.Optional;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Test class for the TagResource REST controller.
 *
 * @see TagResource
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class, TestSecurityConfiguration.class})
@Transactional
class TagResourceIntTest extends BaseTest {

    private static final String API_TAGS_BY_USER = "/api/tags/{createdBy}";
    private static final String INVALID_USER = "invalid_user";
    private static final String SYSTEM_USER = "system";

    private final TagRepository tagRepository;
    private final EntityManager em;

    @MockitoBean
    private SecurityUtils securityUtils;

    private Tag tag;

    @Autowired
    public TagResourceIntTest(TagRepository tagRepository, EntityManager em) {
        this.tagRepository = tagRepository;
        this.em = em;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        em.persist(new IdamDetails("system"));
        em.persist(new IdamDetails("anonymous"));
        tag = createEntity();

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of(SYSTEM_USER));
    }

    public static Tag createEntity() {
        Tag tag = new Tag();
        tag.setName("new_tag");
        tag.setLabel("new tag");
        tag.setCreatedBy(SYSTEM_USER);
        return tag;
    }

    @Test
    void getAllTagsByUser() throws Exception {
        tagRepository.saveAndFlush(tag);

        restLogoutMockMvc.perform(get(API_TAGS_BY_USER, tag.getCreatedBy()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].name").value(hasItem(tag.getName())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(tag.getLabel())));
    }

    @Test
    void getTagsNonExistentUser() throws Exception {
        restLogoutMockMvc.perform(get(API_TAGS_BY_USER, INVALID_USER))
            .andExpect(status().isNotFound());
    }

    @Test
    void getTagsFailsOnUserMismatch() throws Exception {
        tagRepository.saveAndFlush(tag);

        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("other_user"));

        restLogoutMockMvc.perform(get(API_TAGS_BY_USER, tag.getCreatedBy()))
            .andExpect(status().isNotFound());
    }
}