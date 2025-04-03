package uk.gov.hmcts.reform.em.annotation;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.postgresql.util.PSQLException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.em.annotation.rest.AnnotationResource;
import uk.gov.hmcts.reform.em.annotation.service.AnnotationService;
import uk.gov.hmcts.reform.em.annotation.service.CcdService;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Provider("annotation_api")
@ExtendWith(SpringExtension.class)
//Uncomment @PactFolder and comment the @PactBroker line to test local consumer.
//using this, import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
@PactFolder("pacts")
//@PactBroker(
//    url = "${PACT_BROKER_FULL_URL:http://localhost:80}",
//    consumerVersionSelectors = {
//        @VersionSelector(tag = "master")}
//)
@IgnoreNoPactsToVerify
class PostAnnotationsProviderTest {

    private AnnotationService annotationService;
    private CcdService ccdService;

    @BeforeEach
    void before() {
        annotationService = mock(AnnotationService.class);
        ccdService = mock(CcdService.class);

        MockMvcTestTarget testTarget = new MockMvcTestTarget();
        testTarget.setControllers(new AnnotationResource(annotationService, ccdService));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        if (context != null) {
            context.verifyInteraction();
        }
    }

    @State({"annotation is created successfully"})
    public void createAnnotation() throws PSQLException {
        AnnotationDTO annotationDTO = new AnnotationDTO();
        annotationDTO.setId(UUID.randomUUID());
        annotationDTO.setDocumentId(String.valueOf(UUID.randomUUID()));
        annotationDTO.setAnnotationType("Test Annotation");
        annotationDTO.setPage(1);
        annotationDTO.setColor("red");
        annotationDTO.setCaseId(UUID.randomUUID().toString());
        annotationDTO.setJurisdiction("Test Jurisdiction");

        when(annotationService.save(any(AnnotationDTO.class))).thenReturn(annotationDTO);
    }
}