package uk.gov.hmcts.reform.em.annotation.provider;

import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.em.annotation.rest.FilterAnnotationSet;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationSetService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationSetDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@Provider("annotation_api_filter_annotation_set_provider")
@WebMvcTest(value = FilterAnnotationSet.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class,
    OAuth2ClientAutoConfiguration.class
})
public class FilterAnnotationSetProviderTest extends BaseProviderTest {

    private final FilterAnnotationSet filterAnnotationSet;

    @MockitoBean
    private AnnotationSetService annotationSetService;

    private static final String EXAMPLE_DOCUMENT_ID = "f401727b-5a50-40bb-ac4d-87dc34910b6e";
    private static final UUID EXAMPLE_ANNOTATION_SET_ID = UUID.fromString("4f6fe7a2-b8a6-4f0a-9f7c-8d9e1b0c9b3a");

    @Autowired
    public FilterAnnotationSetProviderTest(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            FilterAnnotationSet filterAnnotationSet
    ) {
        super(mockMvc, objectMapper);
        this.filterAnnotationSet = filterAnnotationSet;
    }

    @Override
    protected Object[] getControllersUnderTest() {
        return new Object[]{filterAnnotationSet};
    }

    @State({"an annotation set exists for the given document id"})
    public void filterAnnotationSetByDocumentId() {
        AnnotationSetDTO annotationSetDto = createAnnotationSetDTO(EXAMPLE_ANNOTATION_SET_ID);
        when(annotationSetService.findOneByDocumentId(EXAMPLE_DOCUMENT_ID))
            .thenReturn(Optional.of(annotationSetDto));
    }

    private AnnotationSetDTO createAnnotationSetDTO(UUID annotationSetId) {
        AnnotationSetDTO dto = new AnnotationSetDTO();
        dto.setId(annotationSetId);
        dto.setDocumentId(EXAMPLE_DOCUMENT_ID);

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