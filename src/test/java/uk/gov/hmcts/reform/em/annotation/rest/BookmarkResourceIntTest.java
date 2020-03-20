package uk.gov.hmcts.reform.em.annotation.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.BaseTest;
import uk.gov.hmcts.reform.em.annotation.domain.Bookmark;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.repository.BookmarkRepository;
import uk.gov.hmcts.reform.em.annotation.rest.errors.ExceptionTranslator;
import uk.gov.hmcts.reform.em.annotation.service.BookmarkService;
import uk.gov.hmcts.reform.em.annotation.service.dto.BookmarkDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.BookmarkMapper;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the CommentResource REST controller.
 *
 * @see BookmarkResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class BookmarkResourceIntTest extends BaseTest {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private BookmarkMapper bookmarkMapper;

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private Bookmark bookmark;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BookmarkResource bookmarkResource = new BookmarkResource(bookmarkService);
        em.persist(new IdamDetails("system"));
        em.persist(new IdamDetails("anonymous"));
    }

    public static Bookmark createEntity(EntityManager em) {
        Bookmark bookmark = new Bookmark();
        bookmark.setId(UUID.randomUUID());
        bookmark.setDocumentId(UUID.randomUUID());
        bookmark.setCreatedBy("bob");
        bookmark.setName("My Bookmark");
        bookmark.setNum(426);
        bookmark.setxCoordinate(32.7);
        bookmark.setyCoordinate(100.9);
        return bookmark;
    }

    @Before
    public void initTest() {
        bookmark = createEntity(em);
    }

    @Test
    @Transactional
    public void createBookmarkUUIDNull() throws Exception {
        int databaseSizeBeforeCreate = bookmarkRepository.findAll().size();

        // Create the Comment
        BookmarkDTO bookmarkDTO = bookmarkMapper.toDto(bookmark);
        bookmarkDTO.setId(null);
        restLogoutMockMvc.perform(post("/api/bookmarks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bookmarkDTO)))
                .andExpect(status().isBadRequest());

        // Validate the Comment in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeCreate);
    }
}
