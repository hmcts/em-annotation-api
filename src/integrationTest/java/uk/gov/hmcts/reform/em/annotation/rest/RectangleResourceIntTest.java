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
import uk.gov.hmcts.reform.em.annotation.domain.Annotation;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.domain.Rectangle;
import uk.gov.hmcts.reform.em.annotation.repository.RectangleRepository;
import uk.gov.hmcts.reform.em.annotation.service.dto.RectangleDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.RectangleMapper;

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
 * Test class for the RectangleResource REST controller.
 *
 * @see RectangleResource
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class, TestSecurityConfiguration.class})
class RectangleResourceIntTest extends BaseTest {

    private static final Double DEFAULT_X = 1d;
    private static final Double UPDATED_X = 2d;

    private static final Double DEFAULT_Y = 1d;
    private static final Double UPDATED_Y = 2d;

    private static final Double DEFAULT_WIDTH = 1d;
    private static final Double UPDATED_WIDTH = 2d;

    private static final Double DEFAULT_HEIGHT = 1d;
    private static final Double UPDATED_HEIGHT = 2d;

    @Autowired
    private RectangleRepository rectangleRepository;

    @Autowired
    private RectangleMapper rectangleMapper;

    @Autowired
    private EntityManager em;

    private Rectangle rectangle;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        em.persist(new IdamDetails("system"));
        em.persist(new IdamDetails("anonymous"));
        rectangle = createEntity();
    }

    /**
     * Create an entity for this test.
     *
     * <p>This is a static method, as tests for other entities might also need it,</p>
     * if they test an entity which requires the current entity.
     */
    public static Rectangle createEntity() {
        Rectangle rectangle = new Rectangle()
            .x(DEFAULT_X)
            .y(DEFAULT_Y)
            .width(DEFAULT_WIDTH)
            .height(DEFAULT_HEIGHT);
        rectangle.setId(UUID.randomUUID());
        return rectangle;
    }

    @Test
    @Transactional
    void createRectangleUuidNull() throws Exception {
        int databaseSizeBeforeCreate = rectangleRepository.findAll().size();

        // Create the Rectangle
        RectangleDTO rectangleDTO = rectangleMapper.toDto(rectangle);
        rectangleDTO.setId(null);
        restLogoutMockMvc.perform(post("/api/rectangles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(rectangleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Rectangle in the database
        List<Rectangle> rectangleList = rectangleRepository.findAll();
        assertThat(rectangleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void test_negative_annotation_id_format() throws Exception {

        // Create the Rectangle without an annotation Id
        rectangle.setId(UUID.randomUUID());
        RectangleDTO rectangleDTO = rectangleMapper.toDto(rectangle);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLogoutMockMvc.perform(post("/api/rectangles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(rectangleDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void test_negative_non_existant_annotation_id() throws Exception {
        Annotation annotation = new Annotation();
        annotation.setId(UUID.randomUUID());

        // Create the Rectangle without an non existant annotation Id
        rectangle.setAnnotation(annotation);
        rectangle.setId(UUID.randomUUID());
        RectangleDTO rectangleDTO = rectangleMapper.toDto(rectangle);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLogoutMockMvc.perform(post("/api/rectangles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(rectangleDTO)))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void getAllRectangles() throws Exception {
        // Initialize the database
        rectangleRepository.saveAndFlush(rectangle);

        // Get all the rectangleList
        restLogoutMockMvc.perform(get("/api/rectangles?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rectangle.getId().toString())))
            .andExpect(jsonPath("$.[*].x").value(hasItem(DEFAULT_X)))
            .andExpect(jsonPath("$.[*].y").value(hasItem(DEFAULT_Y)))
            .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
            .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT)));
    }
    
    @Test
    @Transactional
    void getRectangle() throws Exception {
        // Initialize the database
        rectangleRepository.saveAndFlush(rectangle);

        // Get the rectangle
        restLogoutMockMvc.perform(get("/api/rectangles/{id}", rectangle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(rectangle.getId().toString()))
            .andExpect(jsonPath("$.x").value(DEFAULT_X))
            .andExpect(jsonPath("$.y").value(DEFAULT_Y))
            .andExpect(jsonPath("$.width").value(DEFAULT_WIDTH))
            .andExpect(jsonPath("$.height").value(DEFAULT_HEIGHT));
    }

    @Test
    @Transactional
    void getNonExistingRectangle() throws Exception {
        // Get the rectangle
        restLogoutMockMvc.perform(get("/api/rectangles/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateRectangle() throws Exception {
        // Initialize the database
        rectangleRepository.saveAndFlush(rectangle);

        int databaseSizeBeforeUpdate = rectangleRepository.findAll().size();
        assertThat(databaseSizeBeforeUpdate).isPositive();
        // Update the rectangle
        Rectangle updatedRectangle = rectangleRepository.findById(rectangle.getId()).get();
        // Disconnect from session so that the updates on updatedRectangle are not directly saved in db
        em.detach(updatedRectangle);
        updatedRectangle
            .x(UPDATED_X)
            .y(UPDATED_Y)
            .width(UPDATED_WIDTH)
            .height(UPDATED_HEIGHT);
        RectangleDTO rectangleDTO = rectangleMapper.toDto(updatedRectangle);

        restLogoutMockMvc.perform(put("/api/rectangles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(rectangleDTO)))
            .andExpect(status().isOk());

        // Validate the Rectangle in the database
        List<Rectangle> rectangleList = rectangleRepository.findAll();
        assertThat(rectangleList).hasSize(databaseSizeBeforeUpdate);
        Rectangle testRectangle = rectangleList.getLast();
        assertThat(testRectangle.getX()).isEqualTo(UPDATED_X);
        assertThat(testRectangle.getY()).isEqualTo(UPDATED_Y);
        assertThat(testRectangle.getWidth()).isEqualTo(UPDATED_WIDTH);
        assertThat(testRectangle.getHeight()).isEqualTo(UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    void updateNonExistingRectangle() throws Exception {
        int databaseSizeBeforeUpdate = rectangleRepository.findAll().size();

        // Create the Rectangle
        rectangle.setId(null);
        RectangleDTO rectangleDTO = rectangleMapper.toDto(rectangle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLogoutMockMvc.perform(put("/api/rectangles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(rectangleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Rectangle in the database
        List<Rectangle> rectangleList = rectangleRepository.findAll();
        assertThat(rectangleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRectangle() throws Exception {
        // Initialize the database
        rectangleRepository.saveAndFlush(rectangle);

        int databaseSizeBeforeDelete = rectangleRepository.findAll().size();

        // Delete the rectangle
        restLogoutMockMvc.perform(delete("/api/rectangles/{id}", rectangle.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Rectangle> rectangleList = rectangleRepository.findAll();
        assertThat(rectangleList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    void deleteNonExistingRectangle() throws Exception {
        int databaseSizeBeforeDelete = rectangleRepository.findAll().size();

        // Delete the rectangle
        restLogoutMockMvc.perform(delete("/api/rectangles/{id}", rectangle.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database hasn't changed
        List<Rectangle> rectangleList = rectangleRepository.findAll();
        assertThat(rectangleList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Rectangle.class);
        Rectangle rectangle1 = new Rectangle();
        rectangle1.setId(UUID.randomUUID());
        Rectangle rectangle2 = new Rectangle();
        rectangle2.setId(rectangle1.getId());
        assertThat(rectangle1).isEqualTo(rectangle2);
        rectangle2.setId(UUID.randomUUID());
        assertThat(rectangle1).isNotEqualTo(rectangle2);
        rectangle1.setId(null);
        assertThat(rectangle1).isNotEqualTo(rectangle2);
    }

    @Test
    @Transactional
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RectangleDTO.class);
        RectangleDTO rectangleDTO1 = new RectangleDTO();
        rectangleDTO1.setId(UUID.randomUUID());
        RectangleDTO rectangleDTO2 = new RectangleDTO();
        assertThat(rectangleDTO1).isNotEqualTo(rectangleDTO2);
        rectangleDTO2.setId(rectangleDTO1.getId());
        assertThat(rectangleDTO1).isEqualTo(rectangleDTO2);
        rectangleDTO2.setId(UUID.randomUUID());
        assertThat(rectangleDTO1).isNotEqualTo(rectangleDTO2);
        rectangleDTO1.setId(null);
        assertThat(rectangleDTO1).isNotEqualTo(rectangleDTO2);
    }

    @Test
    @Transactional
    void testEntityFromId() {
        UUID uuid = UUID.randomUUID();
        assertThat(rectangleMapper.fromId(uuid).getId()).isEqualTo(uuid);
        assertThat(rectangleMapper.fromId(null)).isNull();
    }
}
