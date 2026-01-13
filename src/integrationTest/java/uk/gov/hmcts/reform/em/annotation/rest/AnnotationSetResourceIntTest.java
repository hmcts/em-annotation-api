package uk.gov.hmcts.reform.em.annotation.rest;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.BaseTest;
import uk.gov.hmcts.reform.em.annotation.domain.AnnotationSet;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationSetRepository;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationSetDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.AnnotationSetMapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the AnnotationSetResource REST controller.
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class, TestSecurityConfiguration.class})
class AnnotationSetResourceIntTest extends BaseTest {

    private static final String DEFAULT_DOCUMENT_ID = "AAAAAAAAAA";
    private static final String UPDATED_DOCUMENT_ID = "BBBBBBBBBB";

    @Autowired
    private AnnotationSetRepository annotationSetRepository;

    @Autowired
    private AnnotationSetMapper annotationSetMapper;

    @Autowired
    private EntityManager em;

    private AnnotationSet annotationSet;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        em.persist(new IdamDetails("system"));
        em.persist(new IdamDetails("anonymous"));
    }

    /**
     * Create an entity for this test.
     *
     * <p>This is a static method, as tests for other entities might also need it,</p>
     * if they test an entity which requires the current entity.
     */
    public static AnnotationSet createEntity() {
        AnnotationSet annotationSet = new AnnotationSet()
            .documentId(DEFAULT_DOCUMENT_ID);

        annotationSet.setId(UUID.randomUUID());
        return annotationSet;
    }

    @BeforeEach
    void initTest() {
        annotationSet = createEntity();
    }

    @Test
    @Transactional
    void createAnnotationSetEmptyUUID() throws Exception {
        int databaseSizeBeforeCreate = annotationSetRepository.findAll().size();

        // Create the AnnotationSet
        AnnotationSetDTO annotationSetDTO = annotationSetMapper.toDto(annotationSet);
        annotationSetDTO.setId(null);
        restLogoutMockMvc.perform(post("/api/annotation-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(annotationSetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AnnotationSet in the database
        List<AnnotationSet> annotationSetList = annotationSetRepository.findAll();
        assertThat(annotationSetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void createAnnotationSetWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = annotationSetRepository.findAll().size();

        // Create the AnnotationSet with an existing ID
        annotationSet.setId(UUID.randomUUID());
        AnnotationSetDTO annotationSetDTO = annotationSetMapper.toDto(annotationSet);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLogoutMockMvc.perform(post("/api/annotation-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(annotationSetDTO)))
            .andExpect(status().isCreated());

        // Validate the AnnotationSet in the database
        List<AnnotationSet> annotationSetList = annotationSetRepository.findAll();
        assertThat(annotationSetList).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    void getAllAnnotationSets() throws Exception {
        // Initialize the database
        annotationSetRepository.saveAndFlush(annotationSet);

        // Get all the annotationSetList
        restLogoutMockMvc.perform(get("/api/annotation-sets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(annotationSet.getId().toString())))
            .andExpect(jsonPath("$.[*].documentId").value(hasItem(DEFAULT_DOCUMENT_ID)));
    }

    @Test
    @Transactional
    void getAnnotationSet() throws Exception {
        // Initialize the database
        annotationSetRepository.saveAndFlush(annotationSet);

        // Get the annotationSet
        restLogoutMockMvc.perform(get("/api/annotation-sets/{id}", annotationSet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(annotationSet.getId().toString()))
            .andExpect(jsonPath("$.documentId").value(DEFAULT_DOCUMENT_ID));
    }

    @Test
    @Transactional
    void getNonExistingAnnotationSet() throws Exception {
        // Get the annotationSet
        restLogoutMockMvc.perform(get("/api/annotation-sets/{id}", UUID.randomUUID()))
            .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    void updateAnnotationSet() throws Exception {
        // Initialize the database
        annotationSetRepository.saveAndFlush(annotationSet);

        int databaseSizeBeforeUpdate = annotationSetRepository.findAll().size();
        assertThat(databaseSizeBeforeUpdate).isPositive();
        // Update the annotationSet
        AnnotationSet updatedAnnotationSet = annotationSetRepository.findById(annotationSet.getId()).get();
        // Disconnect from session so that the updates on updatedAnnotationSet are not directly saved in db
        em.detach(updatedAnnotationSet);
        updatedAnnotationSet
            .documentId(UPDATED_DOCUMENT_ID);
        AnnotationSetDTO annotationSetDTO = annotationSetMapper.toDto(updatedAnnotationSet);

        restLogoutMockMvc.perform(put("/api/annotation-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(annotationSetDTO)))
            .andExpect(status().isOk());

        // Validate the AnnotationSet in the database
        List<AnnotationSet> annotationSetList = annotationSetRepository.findAll();
        assertThat(annotationSetList).hasSize(databaseSizeBeforeUpdate);
        AnnotationSet testAnnotationSet = annotationSetList.getLast();
        assertThat(testAnnotationSet.getDocumentId()).isEqualTo(UPDATED_DOCUMENT_ID);
    }

    @Test
    @Transactional
    void updateNonExistingAnnotationSet() throws Exception {
        int databaseSizeBeforeUpdate = annotationSetRepository.findAll().size();

        annotationSet.setId(null);
        // Create the AnnotationSet
        AnnotationSetDTO annotationSetDTO = annotationSetMapper.toDto(annotationSet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLogoutMockMvc.perform(put("/api/annotation-sets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(annotationSetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AnnotationSet in the database
        List<AnnotationSet> annotationSetList = annotationSetRepository.findAll();
        assertThat(annotationSetList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAnnotationSet() throws Exception {
        // Initialize the database
        annotationSetRepository.saveAndFlush(annotationSet);

        int databaseSizeBeforeDelete = annotationSetRepository.findAll().size();

        // delete the annotationSet
        restLogoutMockMvc.perform(delete("/api/annotation-sets/{id}", annotationSet.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<AnnotationSet> annotationSetList = annotationSetRepository.findAll();
        assertThat(annotationSetList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    void deleteNonExistingAnnotationSet() throws Exception {
        int databaseSizeBeforeDelete = annotationSetRepository.findAll().size();

        // Delete the annotationSet
        restLogoutMockMvc.perform(delete("/api/annotation-sets/{id}", UUID.randomUUID())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database hasn't changed
        List<AnnotationSet> annotationSetList = annotationSetRepository.findAll();
        assertThat(annotationSetList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AnnotationSet.class);
        AnnotationSet annotationSet1 = new AnnotationSet();
        annotationSet1.setId(UUID.randomUUID());
        AnnotationSet annotationSet2 = new AnnotationSet();
        annotationSet2.setId(annotationSet1.getId());
        assertThat(annotationSet1).isEqualTo(annotationSet2);
        annotationSet2.setId(UUID.randomUUID());
        assertThat(annotationSet1).isNotEqualTo(annotationSet2);
        annotationSet1.setId(null);
        assertThat(annotationSet1).isNotEqualTo(annotationSet2);
    }

    @Test
    @Transactional
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AnnotationSetDTO.class);
        AnnotationSetDTO annotationSetDTO1 = new AnnotationSetDTO();
        annotationSetDTO1.setId(UUID.randomUUID());
        AnnotationSetDTO annotationSetDTO2 = new AnnotationSetDTO();
        assertThat(annotationSetDTO1).isNotEqualTo(annotationSetDTO2);
        annotationSetDTO2.setId(annotationSetDTO1.getId());
        assertThat(annotationSetDTO1).isEqualTo(annotationSetDTO2);
        annotationSetDTO2.setId(UUID.randomUUID());
        assertThat(annotationSetDTO1).isNotEqualTo(annotationSetDTO2);
        annotationSetDTO1.setId(null);
        assertThat(annotationSetDTO1).isNotEqualTo(annotationSetDTO2);
    }

    @Test
    @Transactional
    void testEntityFromId() {
        UUID id = UUID.randomUUID();
        assertThat(annotationSetMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(annotationSetMapper.fromId(null)).isNull();
    }
}
