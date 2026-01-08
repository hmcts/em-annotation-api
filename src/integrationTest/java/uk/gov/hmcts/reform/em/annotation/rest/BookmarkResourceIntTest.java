package uk.gov.hmcts.reform.em.annotation.rest;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.BaseTest;
import uk.gov.hmcts.reform.em.annotation.config.security.SecurityUtils;
import uk.gov.hmcts.reform.em.annotation.domain.Bookmark;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.repository.BookmarkRepository;
import uk.gov.hmcts.reform.em.annotation.rest.errors.ExceptionTranslator;
import uk.gov.hmcts.reform.em.annotation.service.BookmarkService;
import uk.gov.hmcts.reform.em.annotation.service.dto.BookmarkDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.DeleteBookmarkDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.BookmarkMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the CommentResource REST controller.
 *
 * @see BookmarkResource
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class, TestSecurityConfiguration.class})
class BookmarkResourceIntTest extends BaseTest {

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

    @MockitoBean
    private SecurityUtils securityUtils;

    private Bookmark bookmark;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        em.persist(new IdamDetails("system"));
        em.persist(new IdamDetails("anonymous"));
    }

    public static Bookmark createEntity() {
        Bookmark bookmark = new Bookmark();
        bookmark.setId(UUID.randomUUID());
        bookmark.setDocumentId(UUID.randomUUID());
        bookmark.setCreatedBy("bob");
        bookmark.setName("My Bookmark");
        bookmark.setPageNumber(426);
        bookmark.setxCoordinate(32.7);
        bookmark.setyCoordinate(100.9);
        return bookmark;
    }

    @BeforeEach
    void initTest() {
        bookmark = createEntity();
    }

    @Test
    @Transactional
    void createBookmarkUuidNull() throws Exception {
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

    @Test
    @Transactional
    void createBookmarkFailsPageNumberValidation() throws Exception {
        int databaseSizeBeforeCreate = bookmarkRepository.findAll().size();

        // Create the Comment
        BookmarkDTO bookmarkDTO = bookmarkMapper.toDto(bookmark);
        bookmarkDTO.setPageNumber(-1);
        restLogoutMockMvc.perform(post("/api/bookmarks")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(bookmarkDTO)))
                .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.entityName").value("bookmark"))
            .andExpect(jsonPath("$.errorKey").value("invalidPageNumber"))
            .andExpect(jsonPath("$.type").value("/problem-with-message"))
            .andExpect(jsonPath("$.title").value("Page number must not be negative or null"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("error.invalidPageNumber"))
            .andExpect(jsonPath("$.params").value("bookmark"));

        // Validate the Comment in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void createBookmarkCreatedByNull() throws Exception {
        int databaseSizeBeforeCreate = bookmarkRepository.findAll().size();
        assertThat(databaseSizeBeforeCreate).isZero();
        bookmark.setCreatedBy(null);
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("fabio"));

        // Create the Comment
        bookmark.setId(UUID.randomUUID());
        BookmarkDTO bookmarkDTO = bookmarkMapper.toDto(bookmark);
        restLogoutMockMvc.perform(post("/api/bookmarks")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(bookmarkDTO)))
                .andExpect(status().isCreated());

        // Validate the Comment in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    void createBookmarkWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bookmarkRepository.findAll().size();

        bookmark.setId(UUID.randomUUID());
        BookmarkDTO bookmarkDTO = bookmarkMapper.toDto(bookmark);

        restLogoutMockMvc.perform(post("/api/bookmarks")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(bookmarkDTO)))
                .andExpect(status().isCreated());

        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    void updateBookmark() throws Exception {
        bookmarkRepository.saveAndFlush(bookmark);

        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();
        assertThat(databaseSizeBeforeUpdate).isPositive();
        Bookmark updatedBookmark = bookmarkRepository.findById(bookmark.getId()).get();

        em.detach(updatedBookmark);
        updatedBookmark
                .setName("Updated Bookmark");
        BookmarkDTO bookmarkDTO = bookmarkMapper.toDto(updatedBookmark);

        restLogoutMockMvc.perform(put("/api/bookmarks")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(bookmarkDTO)))
                .andExpect(status().isOk());

        // Validate the Comment in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeUpdate);
        Bookmark testBookmark = bookmarkList.get(bookmarkList.size() - 1);
        assertThat(testBookmark.getName()).isEqualTo("Updated Bookmark");
    }

    @Test
    @Transactional
    void updateNonExistentBookmark() throws Exception {
        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();
        bookmark.setId(null);

        BookmarkDTO bookmarkDTO = bookmarkMapper.toDto(bookmark);

        restLogoutMockMvc.perform(put("/api/bookmarks")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(bookmarkDTO)))
                .andExpect(status().isBadRequest());

        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void updateMultipleBookmark() throws Exception {
        bookmarkRepository.saveAndFlush(bookmark);

        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();
        assertThat(databaseSizeBeforeUpdate).isPositive();
        Bookmark updatedBookmark = bookmarkRepository.findById(bookmark.getId()).get();

        em.detach(updatedBookmark);
        updatedBookmark.setName("Updated Bookmark");
        BookmarkDTO bookmarkDTO = bookmarkMapper.toDto(updatedBookmark);
        assertThat(bookmarkDTO).isNotNull();
        BookmarkDTO bookmarkDTO1 = bookmarkMapper.toDto(updatedBookmark);
        bookmarkDTO1.setId(UUID.randomUUID());
        bookmarkDTO1.setName("New Bookmark");
        BookmarkDTO bookmarkDTO2 = bookmarkMapper.toDto(updatedBookmark);
        bookmarkDTO2.setId(UUID.randomUUID());
        bookmarkDTO2.setName("Another new Bookmark");

        restLogoutMockMvc.perform(put("/api/bookmarks_multiple")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(Arrays.asList(
                                bookmarkDTO, bookmarkDTO1, bookmarkDTO2))))
                .andExpect(status().isOk());

        // Validate the Comment in the database
        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeUpdate + 2);
        Bookmark testBookmark = bookmarkList.get(bookmarkList.size() - 2);
        assertThat(testBookmark.getName()).isEqualTo("Another new Bookmark");
        testBookmark = bookmarkList.get(bookmarkList.size() - 3);
        assertThat(testBookmark.getName()).isEqualTo("New Bookmark");
    }

    @Test
    @Transactional
    void updateMultipleNonExistentBookmark() throws Exception {
        int databaseSizeBeforeUpdate = bookmarkRepository.findAll().size();
        bookmark.setId(null);

        BookmarkDTO bookmarkDTO = bookmarkMapper.toDto(bookmark);

        restLogoutMockMvc.perform(put("/api/bookmarks_multiple")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(Collections.singletonList(bookmarkDTO))))
                .andExpect(status().isBadRequest());

        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void getBookmarksByDocumentId() throws Exception {
        bookmark = bookmarkRepository.saveAndFlush(bookmark);
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("bob"));

        restLogoutMockMvc.perform(get("/api/" + bookmark.getDocumentId() + "/bookmarks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(bookmark.getId().toString())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(bookmark.getName())));
    }

    @Test
    @Transactional
    void getBookmarksByDocumentIdNoUser() throws Exception {
        bookmark = bookmarkRepository.saveAndFlush(bookmark);

        restLogoutMockMvc.perform(get("/api/" + bookmark.getDocumentId() + "/bookmarks")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(bookmark)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void getBookmarksByDocumentIdNotFound() throws Exception {
        bookmark = bookmarkRepository.saveAndFlush(bookmark);
        when(securityUtils.getCurrentUserLogin()).thenReturn(Optional.of("fabio"));

        restLogoutMockMvc.perform(get("/api/" + bookmark.getDocumentId() + "/bookmarks"))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    void deleteBookmark() throws Exception {
        bookmarkRepository.saveAndFlush(bookmark);

        int databaseSizeBeforeDelete = bookmarkRepository.findAll().size();
        restLogoutMockMvc.perform(delete("/api/bookmarks/{id}", bookmark.getId())
                        .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    void deleteNonExistingBookmark() throws Exception {

        int databaseSizeBeforeDelete = bookmarkRepository.findAll().size();
        restLogoutMockMvc.perform(delete("/api/bookmarks/{id}", UUID.randomUUID())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void deleteMultipleBookmarks() throws Exception {
        bookmarkRepository.saveAndFlush(bookmark);
        Bookmark updatedBookmark = bookmarkRepository.findById(bookmark.getId()).get();

        em.detach(updatedBookmark);
        BookmarkDTO bookmarkDTO = bookmarkMapper.toDto(updatedBookmark);
        assertThat(bookmarkDTO).isNotNull();
        BookmarkDTO bookmarkDTO1 = bookmarkMapper.toDto(updatedBookmark);
        bookmarkDTO1.setId(UUID.randomUUID());
        bookmarkDTO1.setName("New Bookmark");
        bookmarkRepository.saveAndFlush(bookmarkMapper.toEntity(bookmarkDTO1));

        BookmarkDTO bookmarkDTO2 = bookmarkMapper.toDto(updatedBookmark);
        bookmarkDTO2.setId(UUID.randomUUID());
        bookmarkDTO2.setName("Another new Bookmark");
        bookmarkRepository.saveAndFlush(bookmarkMapper.toEntity(bookmarkDTO2));

        BookmarkDTO bookmarkDTO3 = bookmarkMapper.toDto(updatedBookmark);
        bookmarkDTO3.setId(UUID.randomUUID());
        bookmarkDTO3.setName("A Bookmark");
        bookmarkRepository.saveAndFlush(bookmarkMapper.toEntity(bookmarkDTO3));

        int databaseSizeBeforeDelete = bookmarkRepository.findAll().size();
        assertThat(databaseSizeBeforeDelete).isPositive();
        DeleteBookmarkDTO deleteBookmarkDTO = new DeleteBookmarkDTO();
        deleteBookmarkDTO.setUpdated(bookmarkDTO3);
        deleteBookmarkDTO.setDeleted(Arrays.asList(bookmarkDTO.getId(), bookmarkDTO1.getId(), bookmarkDTO2.getId()));

        restLogoutMockMvc.perform(delete("/api/bookmarks_multiple")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(deleteBookmarkDTO)))
                .andExpect(status().isOk());

        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeDelete - 3);
    }

    @Test
    @Transactional
    void deleteMultipleNullBookmarkId() throws Exception {
        int databaseSizeBeforeDelete = bookmarkRepository.findAll().size();
        assertThat(databaseSizeBeforeDelete).isZero();
        bookmark.setId(null);
        BookmarkDTO bookmarkDTO = bookmarkMapper.toDto(bookmark);
        DeleteBookmarkDTO deleteBookmarkDTO = new DeleteBookmarkDTO();
        deleteBookmarkDTO.setDeleted(Collections.singletonList(bookmarkDTO.getId()));

        restLogoutMockMvc.perform(delete("/api/bookmarks_multiple")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(deleteBookmarkDTO)))
                .andExpect(status().isBadRequest());

        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void deleteMultipleNonExistantBookmarkId() throws Exception {
        int databaseSizeBeforeDelete = bookmarkRepository.findAll().size();
        assertThat(databaseSizeBeforeDelete).isZero();
        bookmark.setId(UUID.randomUUID());
        BookmarkDTO bookmarkDTO = bookmarkMapper.toDto(bookmark);
        DeleteBookmarkDTO deleteBookmarkDTO = new DeleteBookmarkDTO();
        deleteBookmarkDTO.setDeleted(Collections.singletonList(bookmarkDTO.getId()));

        restLogoutMockMvc.perform(delete("/api/bookmarks_multiple")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(deleteBookmarkDTO)))
            .andExpect(status().isOk());

        List<Bookmark> bookmarkList = bookmarkRepository.findAll();
        assertThat(bookmarkList).hasSize(databaseSizeBeforeDelete);
    }
}
