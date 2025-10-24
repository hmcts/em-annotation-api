package uk.gov.hmcts.reform.em.annotation.provider;

import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.em.annotation.rest.RectangleResource;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationService;
import uk.gov.hmcts.reform.em.annotation.service.RectangleService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.RectangleDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@Provider("annotation_api_rectangle_provider")
@WebMvcTest(value = RectangleResource.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class,
    OAuth2ClientAutoConfiguration.class
})
public class RectangleProviderTest extends BaseProviderTest {

    private final RectangleResource rectangleResource;

    @MockitoBean
    private RectangleService rectangleService;

    @MockitoBean
    private AnnotationService annotationService;

    private static final UUID EXAMPLE_RECTANGLE_ID = UUID.fromString("a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d");
    private static final UUID EXAMPLE_ANNOTATION_ID = UUID.fromString("d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a");

    @Autowired
    public RectangleProviderTest(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            RectangleResource rectangleResource) {
        super(mockMvc, objectMapper);
        this.rectangleResource = rectangleResource;
    }


    @Override
    protected Object[] getControllersUnderTest() {
        return new Object[]{rectangleResource};
    }

    @State({"rectangle is created successfully"})
    public void createRectangle() {
        RectangleDTO rectangleDto = createRectangleDTO();
        AnnotationDTO mockAnnotation = new AnnotationDTO();
        mockAnnotation.setId(EXAMPLE_ANNOTATION_ID);

        when(annotationService.findOne(EXAMPLE_ANNOTATION_ID)).thenReturn(Optional.of(mockAnnotation));
        when(rectangleService.save(any(RectangleDTO.class))).thenReturn(rectangleDto);
    }

    @State({"rectangle is updated successfully"})
    public void updateRectangle() {
        RectangleDTO rectangleDto = createRectangleDTO();
        when(rectangleService.save(any(RectangleDTO.class))).thenReturn(rectangleDto);
    }

    @State({"rectangles exist"})
    public void getAllRectangles() {
        RectangleDTO rectangleDto1 = createRectangleDTO();
        RectangleDTO rectangleDto2 = createRectangleDTO();
        rectangleDto2.setId(UUID.randomUUID());

        Page<RectangleDTO> page = new PageImpl<>(List.of(rectangleDto1, rectangleDto2));
        when(rectangleService.findAll(any(Pageable.class))).thenReturn(page);
    }

    @State({"a rectangle exists with the given id"})
    public void getRectangleById() {
        RectangleDTO rectangleDto = createRectangleDTO();
        when(rectangleService.findOne(EXAMPLE_RECTANGLE_ID)).thenReturn(Optional.of(rectangleDto));
    }

    @State({"a rectangle exists for deletion"})
    public void deleteRectangle() {
        doNothing().when(rectangleService).delete(EXAMPLE_RECTANGLE_ID);
    }

    private RectangleDTO createRectangleDTO() {
        RectangleDTO dto = new RectangleDTO();
        dto.setId(EXAMPLE_RECTANGLE_ID);
        dto.setAnnotationId(EXAMPLE_ANNOTATION_ID);
        dto.setX(100.5);
        dto.setY(55.2);
        dto.setWidth(250.0);
        dto.setHeight(80.7);

        dto.setCreatedBy(EXAMPLE_USER_ID.toString());
        dto.setCreatedDate(EXAMPLE_DATE);
        dto.setCreatedByDetails(createIdamDetails());
        dto.setLastModifiedBy(EXAMPLE_USER_ID.toString());
        dto.setLastModifiedDate(EXAMPLE_DATE.plusSeconds(120));
        dto.setLastModifiedByDetails(createIdamDetails());

        return dto;
    }
}