package uk.gov.hmcts.reform.em.annotation.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.domain.Rectangle;
import uk.gov.hmcts.reform.em.annotation.repository.RectangleRepository;
import uk.gov.hmcts.reform.em.annotation.service.RectangleService;
import uk.gov.hmcts.reform.em.annotation.service.dto.RectangleDTO;
import uk.gov.hmcts.reform.em.annotation.service.mapper.RectangleMapper;
import uk.gov.hmcts.reform.em.annotation.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.hmcts.reform.em.annotation.rest.TestUtil.createFormattingConversionService;

/**
 * Test class for the RectangleResource REST controller.
 *
 * @see RectangleResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RectangleResourceIntTest {

    private static final Integer DEFAULT_X = 1;
    private static final Integer UPDATED_X = 2;

    private static final Integer DEFAULT_Y = 1;
    private static final Integer UPDATED_Y = 2;

    private static final Integer DEFAULT_WIDTH = 1;
    private static final Integer UPDATED_WIDTH = 2;

    private static final Integer DEFAULT_HEIGHT = 1;
    private static final Integer UPDATED_HEIGHT = 2;

    @Autowired
    private RectangleRepository rectangleRepository;

    @Autowired
    private RectangleMapper rectangleMapper;
    
    @Autowired
    private RectangleService rectangleService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restRectangleMockMvc;

    private Rectangle rectangle;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RectangleResource rectangleResource = new RectangleResource(rectangleService);
        this.restRectangleMockMvc = MockMvcBuilders.standaloneSetup(rectangleResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rectangle createEntity(EntityManager em) {
        Rectangle rectangle = new Rectangle()
            .x(DEFAULT_X)
            .y(DEFAULT_Y)
            .width(DEFAULT_WIDTH)
            .height(DEFAULT_HEIGHT);
        return rectangle;
    }

    @Before
    public void initTest() {
        rectangle = createEntity(em);
    }

    @Test
    @Transactional
    public void createRectangle() throws Exception {
        int databaseSizeBeforeCreate = rectangleRepository.findAll().size();

        // Create the Rectangle
        RectangleDTO rectangleDTO = rectangleMapper.toDto(rectangle);
        restRectangleMockMvc.perform(post("/api/rectangles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(rectangleDTO)))
            .andExpect(status().isCreated());

        // Validate the Rectangle in the database
        List<Rectangle> rectangleList = rectangleRepository.findAll();
        assertThat(rectangleList).hasSize(databaseSizeBeforeCreate + 1);
        Rectangle testRectangle = rectangleList.get(rectangleList.size() - 1);
        assertThat(testRectangle.getX()).isEqualTo(DEFAULT_X);
        assertThat(testRectangle.getY()).isEqualTo(DEFAULT_Y);
        assertThat(testRectangle.getWidth()).isEqualTo(DEFAULT_WIDTH);
        assertThat(testRectangle.getHeight()).isEqualTo(DEFAULT_HEIGHT);
    }

    @Test
    @Transactional
    public void createRectangleWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = rectangleRepository.findAll().size();

        // Create the Rectangle with an existing ID
        rectangle.setId(1L);
        RectangleDTO rectangleDTO = rectangleMapper.toDto(rectangle);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRectangleMockMvc.perform(post("/api/rectangles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(rectangleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Rectangle in the database
        List<Rectangle> rectangleList = rectangleRepository.findAll();
        assertThat(rectangleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllRectangles() throws Exception {
        // Initialize the database
        rectangleRepository.saveAndFlush(rectangle);

        // Get all the rectangleList
        restRectangleMockMvc.perform(get("/api/rectangles?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rectangle.getId().intValue())))
            .andExpect(jsonPath("$.[*].x").value(hasItem(DEFAULT_X)))
            .andExpect(jsonPath("$.[*].y").value(hasItem(DEFAULT_Y)))
            .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
            .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT)));
    }
    
    @Test
    @Transactional
    public void getRectangle() throws Exception {
        // Initialize the database
        rectangleRepository.saveAndFlush(rectangle);

        // Get the rectangle
        restRectangleMockMvc.perform(get("/api/rectangles/{id}", rectangle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(rectangle.getId().intValue()))
            .andExpect(jsonPath("$.x").value(DEFAULT_X))
            .andExpect(jsonPath("$.y").value(DEFAULT_Y))
            .andExpect(jsonPath("$.width").value(DEFAULT_WIDTH))
            .andExpect(jsonPath("$.height").value(DEFAULT_HEIGHT));
    }

    @Test
    @Transactional
    public void getNonExistingRectangle() throws Exception {
        // Get the rectangle
        restRectangleMockMvc.perform(get("/api/rectangles/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRectangle() throws Exception {
        // Initialize the database
        rectangleRepository.saveAndFlush(rectangle);

        int databaseSizeBeforeUpdate = rectangleRepository.findAll().size();

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

        restRectangleMockMvc.perform(put("/api/rectangles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(rectangleDTO)))
            .andExpect(status().isOk());

        // Validate the Rectangle in the database
        List<Rectangle> rectangleList = rectangleRepository.findAll();
        assertThat(rectangleList).hasSize(databaseSizeBeforeUpdate);
        Rectangle testRectangle = rectangleList.get(rectangleList.size() - 1);
        assertThat(testRectangle.getX()).isEqualTo(UPDATED_X);
        assertThat(testRectangle.getY()).isEqualTo(UPDATED_Y);
        assertThat(testRectangle.getWidth()).isEqualTo(UPDATED_WIDTH);
        assertThat(testRectangle.getHeight()).isEqualTo(UPDATED_HEIGHT);
    }

    @Test
    @Transactional
    public void updateNonExistingRectangle() throws Exception {
        int databaseSizeBeforeUpdate = rectangleRepository.findAll().size();

        // Create the Rectangle
        RectangleDTO rectangleDTO = rectangleMapper.toDto(rectangle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRectangleMockMvc.perform(put("/api/rectangles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(rectangleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Rectangle in the database
        List<Rectangle> rectangleList = rectangleRepository.findAll();
        assertThat(rectangleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRectangle() throws Exception {
        // Initialize the database
        rectangleRepository.saveAndFlush(rectangle);

        int databaseSizeBeforeDelete = rectangleRepository.findAll().size();

        // Get the rectangle
        restRectangleMockMvc.perform(delete("/api/rectangles/{id}", rectangle.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Rectangle> rectangleList = rectangleRepository.findAll();
        assertThat(rectangleList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Rectangle.class);
        Rectangle rectangle1 = new Rectangle();
        rectangle1.setId(1L);
        Rectangle rectangle2 = new Rectangle();
        rectangle2.setId(rectangle1.getId());
        assertThat(rectangle1).isEqualTo(rectangle2);
        rectangle2.setId(2L);
        assertThat(rectangle1).isNotEqualTo(rectangle2);
        rectangle1.setId(null);
        assertThat(rectangle1).isNotEqualTo(rectangle2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RectangleDTO.class);
        RectangleDTO rectangleDTO1 = new RectangleDTO();
        rectangleDTO1.setId(1L);
        RectangleDTO rectangleDTO2 = new RectangleDTO();
        assertThat(rectangleDTO1).isNotEqualTo(rectangleDTO2);
        rectangleDTO2.setId(rectangleDTO1.getId());
        assertThat(rectangleDTO1).isEqualTo(rectangleDTO2);
        rectangleDTO2.setId(2L);
        assertThat(rectangleDTO1).isNotEqualTo(rectangleDTO2);
        rectangleDTO1.setId(null);
        assertThat(rectangleDTO1).isNotEqualTo(rectangleDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(rectangleMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(rectangleMapper.fromId(null)).isNull();
    }
}
