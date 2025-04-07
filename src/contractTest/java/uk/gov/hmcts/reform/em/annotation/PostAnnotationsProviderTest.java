package uk.gov.hmcts.reform.em.annotation;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerConsumerVersionSelectors;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.junitsupport.loader.SelectorBuilder;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Provider("annotation_api_annotation_provider")
@ExtendWith(SpringExtension.class)
//Uncomment @PactFolder and comment the @PactBroker line to test local consumer.
//using this, import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
@PactFolder("pacts")
//@PactBroker(
//    url = "${PACT_BROKER_FULL_URL:http://localhost:9292}"
//)
@Import(ContractTestConfiguration.class)
@IgnoreNoPactsToVerify
class PostAnnotationsProviderTest {

    private AnnotationService annotationService;
    private CcdService ccdService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void before(PactVerificationContext context) {
        annotationService = mock(AnnotationService.class);
        ccdService = mock(CcdService.class);

        MockMvcTestTarget testTarget = new MockMvcTestTarget();
        testTarget.setControllers(new AnnotationResource(annotationService, ccdService));

        if (context != null) {
            context.setTarget(testTarget);
        }

        testTarget.setMessageConverters(
            (
                new MappingJackson2HttpMessageConverter(objectMapper)
            )
        );
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        if (context != null) {
            context.verifyInteraction();
        }
    }

    @PactBrokerConsumerVersionSelectors
    public static SelectorBuilder consumerVersionSelectors() {
        return new SelectorBuilder().branch("master");
    }

    @State({"annotation is created successfully"})
    public void createAnnotation() throws PSQLException {

        IdamDetails details = new IdamDetails();
        details.setForename("Test");
        details.setSurname("User");
        details.setEmail("test.user.annotations@example.com");

        CommentDTO exampleComment = new CommentDTO();
        exampleComment.setId(UUID.fromString("dfc7e6a2-1a7c-4b81-a8a1-7da0a1f7c0f1"));
        exampleComment.setAnnotationId(UUID.fromString("e4f8e7b3-2b8d-4c92-b9b2-8eb1b2f8d1f2"));
        exampleComment.setContent("This is a sample annotation comment text which can vary.");
        exampleComment.setCreatedByDetails(details);
        exampleComment.setLastModifiedByDetails(details);
        exampleComment.setCreatedDate(Instant.parse("2024-01-15T10:00:00.123Z"));
        exampleComment.setCreatedBy("c38fd29e-fa2e-43d4-a599-2d3f2908565b");
        exampleComment.setLastModifiedDate(Instant.parse("2024-01-15T11:30:45.678Z"));
        exampleComment.setLastModifiedBy("c38fd29e-fa2e-43d4-a599-2d3f2908565b");

        RectangleDTO exampleRectangle = new RectangleDTO();
        exampleRectangle.setX(100.5);
        exampleRectangle.setWidth(250.0);
        exampleRectangle.setY(55.2);
        exampleRectangle.setId(UUID.fromString("a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"));
        exampleRectangle.setAnnotationId(UUID.fromString("b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e"));
        exampleRectangle.setHeight(80.7);
        exampleRectangle.setCreatedByDetails(details);
        exampleRectangle.setLastModifiedByDetails(details);
        exampleRectangle.setCreatedDate(Instant.parse("2024-01-15T09:45:10.101Z"));
        exampleRectangle.setCreatedBy("c38fd29e-fa2e-43d4-a599-2d3f2908565b");
        exampleRectangle.setLastModifiedDate(Instant.parse("2024-01-15T09:55:20.500Z"));
        exampleRectangle.setLastModifiedBy("c38fd29e-fa2e-43d4-a599-2d3f2908565b");

        TagDTO tagDTO = new TagDTO();
        tagDTO.setCreatedBy("c38fd29e-fa2e-43d4-a599-2d3f2908565b");
        tagDTO.setColor("FFFF00");
        tagDTO.setName("Sample name");
        tagDTO.setLabel("Sample label");

        AnnotationDTO annotationDto = new AnnotationDTO();
        annotationDto.setColor("FFFF00");
        annotationDto.setComments(Set.of(exampleComment));
        annotationDto.setRectangles(Set.of(exampleRectangle));
        annotationDto.setJurisdiction("AB");
        annotationDto.setCommentHeader("Comment Header");
        annotationDto.setAnnotationType(String.valueOf(AnnotationType.HIGHLIGHT));
        annotationDto.setTags(Set.of(tagDTO));
        annotationDto.setAnnotationSetId(UUID.fromString("c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f"));
        annotationDto.setCaseId("123456789012345");
        annotationDto.setId(UUID.fromString("d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a"));
        annotationDto.setPage(1);
        annotationDto.setCreatedByDetails(details);
        annotationDto.setLastModifiedByDetails(details);
        annotationDto.setCreatedDate(Instant.parse("2024-01-15T09:00:00.001Z"));
        annotationDto.setCreatedBy("c38fd29e-fa2e-43d4-a599-2d3f2908565b");
        annotationDto.setLastModifiedDate(Instant.parse("2024-01-15T12:00:00.999Z"));
        annotationDto.setLastModifiedBy("c38fd29e-fa2e-43d4-a599-2d3f2908565b");

        when(annotationService.save(any(AnnotationDTO.class))).thenReturn(annotationDto);
        when(ccdService.buildCommentHeader(any(AnnotationDTO.class), anyString())).thenReturn("Test Comment Header");
        when(annotationService.findOne(any(UUID.class), anyBoolean())).thenReturn(Optional.of(annotationDto));
    }
}