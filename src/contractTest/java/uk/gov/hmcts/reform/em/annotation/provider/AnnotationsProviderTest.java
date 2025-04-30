package uk.gov.hmcts.reform.em.annotation.provider;

import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.reform.em.annotation.domain.IdamDetails;
import uk.gov.hmcts.reform.em.annotation.domain.enumeration.AnnotationType;
import uk.gov.hmcts.reform.em.annotation.rest.AnnotationResource;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationService;
import uk.gov.hmcts.reform.em.annotation.service.CcdService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.CommentDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.RectangleDTO;
import uk.gov.hmcts.reform.em.annotation.service.dto.TagDTO;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@Provider("annotation_api_annotation_provider")
@WebMvcTest(value = AnnotationResource.class, excludeAutoConfiguration = {
    SecurityAutoConfiguration.class,
    OAuth2ClientAutoConfiguration.class
})
public class AnnotationsProviderTest extends BaseProviderTest {

    @Autowired
    private AnnotationResource annotationResource;

    @MockitoBean
    private AnnotationService annotationService;
    @MockitoBean
    private CcdService ccdService;

    @Override
    protected Object[] getControllersUnderTest() {
        return new Object[]{annotationResource};
    }


    @State({"annotation is created successfully"})
    public void createAnnotation() throws PSQLException {

        AnnotationDTO annotationDto = getAnnotationDTO();

        when(annotationService.save(any(AnnotationDTO.class))).thenReturn(annotationDto);
        when(ccdService.buildCommentHeader(any(AnnotationDTO.class), anyString())).thenReturn("Test Comment Header");
        when(annotationService.findOne(any(UUID.class), anyBoolean())).thenReturn(Optional.of(annotationDto));
    }

    @State({"annotation is updated successfully"})
    public void updateAnnotation() throws PSQLException {

        AnnotationDTO annotationDto = getAnnotationDTO();

        when(annotationService.save(any(AnnotationDTO.class))).thenReturn(annotationDto);
    }

    @State({"gets all annotations"})
    public void getAllAnnotations() {
        AnnotationDTO annotationDto = getAnnotationDTO();
        AnnotationDTO annotationDto2 = getAnnotationDTO();
        annotationDto2.setId(UUID.randomUUID());

        Page<AnnotationDTO> page = new PageImpl<>(List.of(annotationDto, annotationDto2));
        when(annotationService.findAll(any(Pageable.class))).thenReturn(page);
    }

    @State({"gets the annotation by given id"})
    public void getAnnotation() {
        AnnotationDTO annotationDto = getAnnotationDTO();
        UUID id = annotationDto.getId();
        when(annotationService.findOne(id)).thenReturn(Optional.of(annotationDto));
    }

    @State({"annotation exists for deletion"})
    public void deleteAnnotation() {
        doNothing().when(annotationService).delete(any(UUID.class));
    }

    public static AnnotationDTO getAnnotationDTO() {
        IdamDetails details = new IdamDetails();
        details.setForename("Test");
        details.setSurname("User");
        details.setEmail("test.user.annotations@example.com");

        String exampleUserIdStr = EXAMPLE_USER_ID.toString();
        Instant exampleCreatedDate = EXAMPLE_DATE;
        Instant exampleModifiedDate = EXAMPLE_DATE.plusSeconds(120);


        CommentDTO exampleComment = new CommentDTO();
        exampleComment.setId(UUID.fromString("dfc7e6a2-1a7c-4b81-a8a1-7da0a1f7c0f1"));
        exampleComment.setAnnotationId(UUID.fromString("e4f8e7b3-2b8d-4c92-b9b2-8eb1b2f8d1f2"));
        exampleComment.setContent("This is a sample annotation comment text which can vary.");
        exampleComment.setCreatedByDetails(details);
        exampleComment.setLastModifiedByDetails(details);
        exampleComment.setCreatedDate(exampleCreatedDate);
        exampleComment.setCreatedBy(exampleUserIdStr);
        exampleComment.setLastModifiedDate(exampleModifiedDate);
        exampleComment.setLastModifiedBy(exampleUserIdStr);

        RectangleDTO exampleRectangle = new RectangleDTO();
        exampleRectangle.setX(100.5);
        exampleRectangle.setWidth(250.0);
        exampleRectangle.setY(55.2);
        exampleRectangle.setId(UUID.fromString("a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"));
        exampleRectangle.setAnnotationId(UUID.fromString("b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e"));
        exampleRectangle.setHeight(80.7);
        exampleRectangle.setCreatedByDetails(details);
        exampleRectangle.setLastModifiedByDetails(details);
        exampleRectangle.setCreatedDate(exampleCreatedDate.minusSeconds(30));
        exampleRectangle.setCreatedBy(exampleUserIdStr);
        exampleRectangle.setLastModifiedDate(exampleModifiedDate.minusSeconds(15));
        exampleRectangle.setLastModifiedBy(exampleUserIdStr);

        TagDTO tagDTO = new TagDTO();
        tagDTO.setCreatedBy(exampleUserIdStr);
        tagDTO.setColor("FFFF00");
        tagDTO.setName("Sample name");
        tagDTO.setLabel("Sample label");

        AnnotationDTO annotationDto = new AnnotationDTO();
        annotationDto.setId(UUID.fromString("d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a"));
        annotationDto.setAnnotationSetId(UUID.fromString("c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f"));
        annotationDto.setColor("FFFF00");
        annotationDto.setComments(Set.of(exampleComment));
        annotationDto.setRectangles(Set.of(exampleRectangle));
        annotationDto.setJurisdiction("AB");
        annotationDto.setCommentHeader("Comment Header");
        annotationDto.setAnnotationType(String.valueOf(AnnotationType.HIGHLIGHT));
        annotationDto.setTags(Set.of(tagDTO));
        annotationDto.setCaseId("123456789012345");
        annotationDto.setPage(1);

        annotationDto.setCreatedByDetails(details);
        annotationDto.setLastModifiedByDetails(details);
        annotationDto.setCreatedDate(exampleCreatedDate);
        annotationDto.setCreatedBy(exampleUserIdStr);
        annotationDto.setLastModifiedDate(exampleModifiedDate);
        annotationDto.setLastModifiedBy(exampleUserIdStr);

        exampleComment.setAnnotationId(annotationDto.getId());
        exampleRectangle.setAnnotationId(annotationDto.getId());


        return annotationDto;
    }
}