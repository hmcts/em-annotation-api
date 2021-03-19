package uk.gov.hmcts.reform.em.annotation.rest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.em.annotation.rest.errors.BadRequestAlertException;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationService;
import uk.gov.hmcts.reform.em.annotation.service.RectangleService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.RectangleDTO;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class RectangleResourceTest {

    @Mock
    private AnnotationService annotationService;

    @Mock
    private RectangleService rectangleService;

    @InjectMocks
    private RectangleResource rectangleResource;

    private UUID documentId = UUID.randomUUID();

    private static final String ENTITY_NAME = "rectangle";

    @BeforeAll
    public void setUp() {

        //rectangleResource = new RectangleResource(rectangleService);
        MockitoAnnotations.initMocks(this);
    }

    private static final Double DEFAULT_X = 1d;
    private static final Double UPDATED_X = 2d;

    private static final Double DEFAULT_Y = 1d;
    private static final Double UPDATED_Y = 2d;

    private static final Double DEFAULT_WIDTH = 1d;
    private static final Double UPDATED_WIDTH = 2d;

    private static final Double DEFAULT_HEIGHT = 1d;
    private static final Double UPDATED_HEIGHT = 2d;

    @Test
    public void test_create_rectangle_data_validation_for_id()
        throws Exception {
        RectangleDTO rectangleDTO = createRectangleDTO();
        rectangleDTO.setId(null);
        try {
            rectangleResource.createRectangle(rectangleDTO);
        } catch (BadRequestAlertException badRequestAlertException) {
            assertEquals(ENTITY_NAME, badRequestAlertException.getEntityName());
            assertEquals("idnull", badRequestAlertException.getErrorKey());
        }
    }

    @Test
    public void test_create_rectangle_data_validation_for_annotation_id()
        throws Exception {
        RectangleDTO rectangleDTO = createRectangleDTO();
        rectangleDTO.setId(UUID.randomUUID());
        rectangleDTO.setAnnotationId(null);
        try {
            rectangleResource.createRectangle(rectangleDTO);
        } catch (BadRequestAlertException badRequestAlertException) {
            assertEquals(ENTITY_NAME, badRequestAlertException.getEntityName());
            assertEquals("idnull", badRequestAlertException.getErrorKey());
        }
    }

    @Test
    public void test_create_rectangle_data_validation_for_non_existant_annotation() throws Exception {

        RectangleDTO rectangleDTO = createRectangleDTO();
        rectangleDTO.setId(UUID.randomUUID());
        rectangleDTO.setAnnotationId(UUID.randomUUID());

        Mockito.when(annotationService.findOne(rectangleDTO.getAnnotationId())).thenReturn(Optional.empty());
        try {
            ResponseEntity<RectangleDTO> responseEntity = rectangleResource.createRectangle(rectangleDTO);
            assertEquals(404, responseEntity.getStatusCodeValue());
        } catch (BadRequestAlertException badRequestAlertException) {
            fail("This is not the expected exception for this test");
        }
    }

    @Test
    public void test_create_rectangle_data_validation_for_non_existant_annotation_id() throws Exception {

        RectangleDTO rectangleDTO = createRectangleDTO();
        rectangleDTO.setId(UUID.randomUUID());
        rectangleDTO.setAnnotationId(UUID.randomUUID());

        Mockito.when(annotationService.findOne(rectangleDTO.getAnnotationId()))
            .thenReturn(Optional.of(new AnnotationDTO()));
        try {
            ResponseEntity<RectangleDTO> responseEntity = rectangleResource.createRectangle(rectangleDTO);
            assertEquals(404, responseEntity.getStatusCodeValue());
        } catch (BadRequestAlertException badRequestAlertException) {
            fail("This is not the expected exception for this test");
        }
    }

    @Test
    public void test_create_rectangle_positive_path() throws Exception {

        AnnotationDTO annotationDTO = new AnnotationDTO();
        annotationDTO.setId(UUID.randomUUID());

        RectangleDTO rectangleDTO = createRectangleDTO();
        rectangleDTO.setId(UUID.randomUUID());
        rectangleDTO.setAnnotationId(annotationDTO.getId());

        Mockito.when(annotationService.findOne(rectangleDTO.getAnnotationId())).thenReturn(Optional.of(annotationDTO));
        Mockito.when(rectangleService.save(any())).thenReturn(rectangleDTO);
        try {
            ResponseEntity<RectangleDTO> responseEntity = rectangleResource.createRectangle(rectangleDTO);
            assertEquals(201, responseEntity.getStatusCodeValue());
        } catch (BadRequestAlertException badRequestAlertException) {
            fail("This is not the expected exception for this test");
        }
    }

    private RectangleDTO createRectangleDTO() {

        RectangleDTO rectangleDTO = new RectangleDTO();
        rectangleDTO.setX(DEFAULT_X);
        rectangleDTO.setY(DEFAULT_Y);
        rectangleDTO.setWidth(DEFAULT_WIDTH);
        rectangleDTO.setHeight(DEFAULT_HEIGHT);
        rectangleDTO.setId(UUID.randomUUID());
        return rectangleDTO;
    }

}
