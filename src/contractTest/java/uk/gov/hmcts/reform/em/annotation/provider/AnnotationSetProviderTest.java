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
import uk.gov.hmcts.reform.em.annotation.rest.AnnotationSetResource;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationSetService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationSetDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@Provider("annotation_api_annotation_set_provider")
@WebMvcTest(value = AnnotationSetResource.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class,
    OAuth2ClientAutoConfiguration.class
})
public class AnnotationSetProviderTest extends BaseProviderTest {

    private final AnnotationSetResource annotationSetResource;

    @MockitoBean
    private AnnotationSetService annotationSetService;

    private static final UUID EXAMPLE_ANNOTATION_SET_ID = UUID.fromString("4f6fe7a2-b8a6-4f0a-9f7c-8d9e1b0c9b3a");

    @Autowired
    public AnnotationSetProviderTest(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            AnnotationSetResource annotationSetResource
    ) {
        super(mockMvc, objectMapper);
        this.annotationSetResource = annotationSetResource;
    }


    @Override
    protected Object[] getControllersUnderTest() {
        return new Object[]{annotationSetResource};
    }


    @State({"annotation set is created successfully"})
    public void createAnnotationSet() {
        AnnotationSetDTO annotationSetDto = createAnnotationSetDTO(EXAMPLE_ANNOTATION_SET_ID);
        when(annotationSetService.save(any(AnnotationSetDTO.class))).thenReturn(annotationSetDto);
        when(annotationSetService.findOne(annotationSetDto.getId())).thenReturn(Optional.of(annotationSetDto));
    }

    @State({"annotation set is updated successfully"})
    public void updateAnnotationSet() {
        AnnotationSetDTO annotationSetDto = createAnnotationSetDTO(EXAMPLE_ANNOTATION_SET_ID);
        when(annotationSetService.save(any(AnnotationSetDTO.class))).thenReturn(annotationSetDto);
    }

    @State({"annotation sets exist"})
    public void getAllAnnotationSets() {
        AnnotationSetDTO set1 = createAnnotationSetDTO(EXAMPLE_ANNOTATION_SET_ID);
        AnnotationSetDTO set2 = createAnnotationSetDTO(UUID.randomUUID());
        Page<AnnotationSetDTO> page = new PageImpl<>(List.of(set1, set2));
        when(annotationSetService.findAll(any(Pageable.class))).thenReturn(page);
    }

    @State({"an annotation set exists with the given id"})
    public void getAnnotationSetById() {
        AnnotationSetDTO annotationSetDto = createAnnotationSetDTO(EXAMPLE_ANNOTATION_SET_ID);
        when(annotationSetService.findOne(EXAMPLE_ANNOTATION_SET_ID)).thenReturn(Optional.of(annotationSetDto));
    }


    @State({"an annotation set exists for deletion"})
    public void deleteAnnotationSet() {
        doNothing().when(annotationSetService).delete(EXAMPLE_ANNOTATION_SET_ID);
    }


    private AnnotationSetDTO createAnnotationSetDTO(UUID annotationSetId) {
        AnnotationSetDTO dto = new AnnotationSetDTO();
        dto.setId(annotationSetId);
        dto.setDocumentId("f401727b-5a50-40bb-ac4d-87dc34910b6e");

        dto.setCreatedBy(EXAMPLE_USER_ID.toString());
        dto.setCreatedDate(EXAMPLE_DATE);
        dto.setCreatedByDetails(createIdamDetails());
        dto.setLastModifiedBy(EXAMPLE_USER_ID.toString());
        dto.setLastModifiedDate(EXAMPLE_DATE.plusSeconds(60));
        dto.setLastModifiedByDetails(createIdamDetails());

        AnnotationDTO mockAnnotation = AnnotationsProviderTest.getAnnotationDTO();
        mockAnnotation.setAnnotationSetId(dto.getId());
        dto.setAnnotations(new HashSet<>(List.of(mockAnnotation)));

        return dto;
    }

}