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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.BaseTest;
import uk.gov.hmcts.reform.em.annotation.domain.Annotation;
import uk.gov.hmcts.reform.em.annotation.domain.Comment;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.domain.Rectangle;
import uk.gov.hmcts.reform.em.annotation.domain.Tag;
import uk.gov.hmcts.reform.em.annotation.domain.enumeration.AnnotationType;
import uk.gov.hmcts.reform.em.annotation.repository.AnnotationRepository;
import uk.gov.hmcts.reform.em.annotation.repository.TagRepository;
import uk.gov.hmcts.reform.em.annotation.rest.errors.ExceptionTranslator;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationSetService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.AnnotationMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
 * Test class for the AnnotationResource REST controller.
 *
 * @see AnnotationResource
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class, TestSecurityConfiguration.class})
class AnnotationResourceIntTest extends BaseTest {

    private static final AnnotationType DEFAULT_ANNOTATION_TYPE = AnnotationType.AREA;
    private static final AnnotationType UPDATED_ANNOTATION_TYPE = AnnotationType.HIGHLIGHT;

    private static final Integer DEFAULT_PAGE = 1;
    private static final Integer UPDATED_PAGE = 2;

    private static final Double DEFAULT_X = 1d;
    private static final Double UPDATED_X = 2d;

    private static final Double DEFAULT_Y = 1d;
    private static final Double UPDATED_Y = 2d;

    private static final Double DEFAULT_WIDTH = 1d;
    private static final Double UPDATED_WIDTH = 2d;

    private static final Double DEFAULT_HEIGHT = 1d;
    private static final Double UPDATED_HEIGHT = 2d;

    @Autowired
    private AnnotationRepository annotationRepository;

    @Autowired
    private AnnotationSetService annotationSetService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private AnnotationMapper annotationMapper;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private Annotation annotation;

    private UUID uuid;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        em.persist(new IdamDetails("system"));
        em.persist(new IdamDetails("anonymous"));
    }

    /**
     * Create an entity for this test.
     *
     * <p>This is a static method, as tests for other entities might also need it,
     *      if they test an entity which requires the current entity.
     */
    public static Annotation createEntity() {
        Annotation annotation = new Annotation()
            .annotationType(DEFAULT_ANNOTATION_TYPE.toString())
            .page(DEFAULT_PAGE);
        annotation.setId(UUID.randomUUID());
        annotation.setCreatedBy("system");
        Tag tag = new Tag();
        tag.setName("new_tag");
        tag.setLabel("new tag");
        tag.setCreatedBy("system");
        annotation.addTag(tag);
        return annotation;
    }

    @BeforeEach
    public void initTest() {
        annotation = createEntity();
    }

    @Test
    @Transactional
    void createAnnotation() throws Exception {
        int databaseSizeBeforeCreate = annotationRepository.findAll().size();

        // Create the Annotation
        AnnotationDTO annotationDTO = annotationMapper.toDto(annotation);
        annotationDTO.setId(null);
        restLogoutMockMvc.perform(post("/api/annotations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(annotationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Annotation in the database
        List<Annotation> annotationList = annotationRepository.findAll();
        assertThat(annotationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void createAnnotationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = annotationRepository.findAll().size();
        assertThat(databaseSizeBeforeCreate).isZero();
        // Create the Annotation with an existing ID
        uuid = UUID.randomUUID();
        annotation.setId(uuid);
        AnnotationDTO annotationDTO = annotationMapper.toDto(annotation);
        annotationDTO.setAnnotationSetId(UUID.randomUUID());
        annotationDTO.setDocumentId("DocId");

        // An entity with an existing ID cannot be created, so this API call must fail
        restLogoutMockMvc.perform(post("/api/annotations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(annotationDTO)))
            .andExpect(status().isCreated());

        // Validate the Annotation in the database
        List<Annotation> annotationList = annotationRepository.findAll();
        assertThat(annotationList).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    void createAnnotationFailsPageNumberValidation() throws Exception {
        int databaseSizeBeforeCreate = annotationRepository.findAll().size();

        // Create the Comment
        AnnotationDTO annotationDTO = annotationMapper.toDto(annotation);
        annotationDTO.setPage(-1);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLogoutMockMvc.perform(post("/api/annotations")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(annotationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"type\":\"/constraint-violation\","
                        + "\"title\":\"Method argument not valid\",\"status\":400,\"path\":\"/api/annotations\","
                        + "\"message\":\"error.validation\",\"fieldErrors\":[{\"field\":\"page\","
                        + "\"message\":\"Min\",\"objectName\":\"annotationDTO\"}]}"));

        // Validate the Comment in the database
        List<Annotation> annotationList = annotationRepository.findAll();
        assertThat(annotationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void createAnnotationWithExistingDocIdAndCreatedBy() throws Exception {
        int databaseSizeBeforeCreate = annotationRepository.findAll().size();
        assertThat(databaseSizeBeforeCreate).isZero();
        // Create the Annotation with an existing ID
        uuid = UUID.randomUUID();
        annotation.setId(uuid);
        AnnotationDTO annotationDTO = annotationMapper.toDto(annotation);
        annotationDTO.setAnnotationSetId(UUID.randomUUID());
        annotationDTO.setDocumentId("DocId");

        restLogoutMockMvc.perform(post("/api/annotations")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(annotationDTO)))
                .andExpect(status().isCreated());

        annotationDTO.setAnnotationSetId(UUID.randomUUID());

        restLogoutMockMvc.perform(post("/api/annotations")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(annotationDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void getAllAnnotations() throws Exception {
        // Initialize the database
        Tag tag = new Tag();
        tag.setName("new_tag");
        tag.setLabel("new tag");
        tag.setCreatedBy("system");
        tagRepository.saveAndFlush(tag);
        annotationRepository.saveAndFlush(annotation);

        // Get all the annotationList
        restLogoutMockMvc.perform(get("/api/annotations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(annotation.getId().toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_ANNOTATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].page").value(hasItem(DEFAULT_PAGE)));
    }
    
    @Test
    @Transactional
    void getAnnotation() throws Exception {
        // Initialize the database
        Tag tag = new Tag();
        tag.setName("new_tag");
        tag.setLabel("new tag");
        tag.setCreatedBy("system");
        tagRepository.saveAndFlush(tag);
        annotationRepository.saveAndFlush(annotation);

        // Get the annotation
        restLogoutMockMvc.perform(get("/api/annotations/{id}", annotation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(annotation.getId().toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_ANNOTATION_TYPE.toString()))
            .andExpect(jsonPath("$.page").value(DEFAULT_PAGE));
    }

    @Test
    @Transactional
    void getNonExistingAnnotation() throws Exception {
        // Get the annotation
        restLogoutMockMvc.perform(get("/api/annotations/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateAnnotation() throws Exception {
        // Initialize the database
        Tag tag = new Tag();
        tag.setName("new_tag");
        tag.setLabel("new tag");
        tag.setCreatedBy("system");
        tagRepository.saveAndFlush(tag);

        // Initialize a Rectangle Set
        Rectangle rectangle = new Rectangle();
        rectangle.setAnnotation(null);
        rectangle.setId(UUID.randomUUID());
        Rectangle rectangle2 = new Rectangle();
        rectangle2.setAnnotation(annotation);
        rectangle2.setId(UUID.randomUUID());
        Set<Rectangle> rectangles = new HashSet<>();
        rectangles.add(rectangle);
        rectangles.add(rectangle2);
        annotation.setRectangles(rectangles);

        // Initialize a Comment Set
        Comment comment = new Comment();
        comment.setAnnotation(null);
        comment.setId(UUID.randomUUID());
        Comment comment2 = new Comment();
        comment2.setAnnotation(annotation);
        comment2.setId(UUID.randomUUID());
        Set<Comment> comments = new HashSet<>();
        comments.add(comment);
        comments.add(comment2);
        annotation.setComments(comments);

        annotationRepository.saveAndFlush(annotation);
        int databaseSizeBeforeUpdate = annotationRepository.findAll().size();
        assertThat(databaseSizeBeforeUpdate).isPositive();
        // Update the annotation
        Annotation updatedAnnotation = annotationRepository.findById(annotation.getId()).get();
        // Disconnect from session so that the updates on updatedAnnotation are not directly saved in db
        em.detach(updatedAnnotation);
        updatedAnnotation
            .annotationType(UPDATED_ANNOTATION_TYPE.toString())
            .page(UPDATED_PAGE);
        AnnotationDTO annotationDTO = annotationMapper.toDto(updatedAnnotation);
        annotationDTO.setAnnotationSetId(UUID.randomUUID());
        annotationDTO.setDocumentId("DocId");

        restLogoutMockMvc.perform(put("/api/annotations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(annotationDTO)))
            .andExpect(status().isOk());

        // Validate the Annotation in the database
        List<Annotation> annotationList = annotationRepository.findAll();
        assertThat(annotationList).hasSize(databaseSizeBeforeUpdate);
        Annotation testAnnotation = annotationList.get(annotationList.size() - 1);
        assertThat(testAnnotation.getAnnotationType()).isEqualTo(UPDATED_ANNOTATION_TYPE.toString());
        assertThat(testAnnotation.getPage()).isEqualTo(UPDATED_PAGE);
    }

    @Test
    @Transactional
    void updateAnnotationWithExistingDocIdAndCreatedBy() throws Exception {
        int databaseSizeBeforeCreate = annotationRepository.findAll().size();
        assertThat(databaseSizeBeforeCreate).isZero();
        // Create the Annotation with an existing ID
        uuid = UUID.randomUUID();
        annotation.setId(uuid);
        AnnotationDTO annotationDTO = annotationMapper.toDto(annotation);
        annotationDTO.setAnnotationSetId(UUID.randomUUID());
        annotationDTO.setDocumentId("DocId");

        restLogoutMockMvc.perform(post("/api/annotations")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(annotationDTO)))
                .andExpect(status().isCreated());

        annotationDTO.setAnnotationSetId(UUID.randomUUID());

        restLogoutMockMvc.perform(put("/api/annotations")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(annotationDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void updateNonExistingAnnotation() throws Exception {
        int databaseSizeBeforeUpdate = annotationRepository.findAll().size();
        assertThat(databaseSizeBeforeUpdate).isZero();
        // Create the Annotation
        AnnotationDTO annotationDTO = annotationMapper.toDto(annotation);
        annotationDTO.setId(null);
        annotationDTO.setAnnotationSetId(UUID.randomUUID());
        annotationDTO.setDocumentId("DocId");

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLogoutMockMvc.perform(put("/api/annotations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(annotationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Annotation in the database
        List<Annotation> annotationList = annotationRepository.findAll();
        assertThat(annotationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAnnotation() throws Exception {
        // Initialize the database
        Tag tag = new Tag();
        tag.setName("new_tag");
        tag.setLabel("new tag");
        tag.setCreatedBy("system");
        tagRepository.saveAndFlush(tag);
        annotationRepository.saveAndFlush(annotation);

        int databaseSizeBeforeDelete = annotationRepository.findAll().size();

        // Delete the annotation
        restLogoutMockMvc.perform(delete("/api/annotations/{id}", annotation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Annotation> annotationList = annotationRepository.findAll();
        assertThat(annotationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    void deleteNonExistingAnnotation() throws Exception {
        int databaseSizeBeforeDelete = annotationRepository.findAll().size();

        // Delete the annotation
        restLogoutMockMvc.perform(delete("/api/annotations/{id}", UUID.randomUUID())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database hasn't changed
        List<Annotation> annotationList = annotationRepository.findAll();
        assertThat(annotationList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Annotation.class);
        Annotation annotation1 = new Annotation();
        annotation1.setId(UUID.randomUUID());
        Annotation annotation2 = new Annotation();
        annotation2.setId(annotation1.getId());
        assertThat(annotation1).isEqualTo(annotation2);
        annotation2.setId(uuid);
        assertThat(annotation1).isNotEqualTo(annotation2);
        annotation1.setId(null);
        assertThat(annotation1).isNotEqualTo(annotation2);
    }

    @Test
    @Transactional
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AnnotationDTO.class);
        AnnotationDTO annotationDTO1 = new AnnotationDTO();
        annotationDTO1.setId(UUID.randomUUID());
        AnnotationDTO annotationDTO2 = new AnnotationDTO();
        assertThat(annotationDTO1).isNotEqualTo(annotationDTO2);
        annotationDTO2.setId(annotationDTO1.getId());
        assertThat(annotationDTO1).isEqualTo(annotationDTO2);
        annotationDTO2.setId(uuid);
        assertThat(annotationDTO1).isNotEqualTo(annotationDTO2);
        annotationDTO1.setId(null);
        assertThat(annotationDTO1).isNotEqualTo(annotationDTO2);
    }

    @Test
    @Transactional
    void testEntityFromId() {
        UUID uuid = UUID.randomUUID();
        assertThat(annotationMapper.fromId(uuid).getId()).isEqualTo(uuid);
        assertThat(annotationMapper.fromId(null)).isNull();
    }
}
