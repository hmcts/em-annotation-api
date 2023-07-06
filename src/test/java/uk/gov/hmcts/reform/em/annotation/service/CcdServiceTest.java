package uk.gov.hmcts.reform.em.annotation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.annotation.config.CommentHeaderConfig;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CcdServiceTest {

    @Mock
    private CoreCaseDataApi coreCaseDataApi;

    @Mock
    private AuthTokenGenerator authTokenGenerator;

    @Mock
    private CommentHeaderConfig commentHeaderConfig;

    @InjectMocks
    private CcdService ccdService;

    private final String caseId = "case_id_test";
    private final String jwt = "jwt_test";
    private final String serviceToken = "service_token";

    private final Map<String, Object> caseData = Map.of("Key1", "Value1",
            "Key2", Map.of("Key21", "Value21"));

    private final CaseDetails caseDetails = CaseDetails.builder()
            .data(caseData)
            .caseTypeId(caseId)
            .callbackResponseStatus("SUCCESS")
            .jurisdiction("TEST_jurisdiction")
            .build();
    private final HashMap<String, ArrayList<String>> jurisdictionPaths = new HashMap<>();

    private AnnotationDTO annotationDTO;

    @BeforeEach
    void setUp() {
        jurisdictionPaths.put("invalid path jurisdiction", new ArrayList<>(List.of("path1", "path2")));
        jurisdictionPaths.put("jurisdiction", new ArrayList<>(List.of("Key1", "Key2.Key21")));

        annotationDTO = new AnnotationDTO();
        annotationDTO.setCommentHeader(null);
        annotationDTO.setCaseId(caseId);
        annotationDTO.setJurisdiction("jurisdiction");
    }

    @Test
    void getCaseDetails() {
        given(authTokenGenerator.generate()).willReturn(serviceToken);
        given(coreCaseDataApi.getCase(jwt, serviceToken, caseId)).willReturn(caseDetails);
        ccdService.getCaseDetails(jwt, caseId);
        verify(coreCaseDataApi).getCase(jwt, serviceToken, caseId);
    }

    @Test
    void commentHeaderWithNullCaseId() {
        annotationDTO.setCaseId(null);
        String commentHeader = ccdService.buildCommentHeader(annotationDTO, jwt);
        assertNull(commentHeader);
        verifyNoInteractions(coreCaseDataApi);
    }

    @Test
    void commentHeaderWithExistingValue() {
        annotationDTO.setCommentHeader("commentHeader");
        String commentHeader = ccdService.buildCommentHeader(annotationDTO, jwt);
        assertEquals("commentHeader", commentHeader);
        verifyNoInteractions(coreCaseDataApi);
    }

    @Test
    void commentHeaderWithInvalidJurisdiction() {
        annotationDTO.setJurisdiction("invalid jurisdiction");
        given(commentHeaderConfig.getJurisdictionPaths()).willReturn(jurisdictionPaths);
        String commentHeader = ccdService.buildCommentHeader(annotationDTO, jwt);
        assertNull(commentHeader);
        verifyNoInteractions(coreCaseDataApi);
    }

    @Test
    void commentHeaderWithInvalidPath() {
        given(authTokenGenerator.generate()).willReturn(serviceToken);
        given(coreCaseDataApi.getCase(jwt, serviceToken, caseId)).willReturn(caseDetails);
        annotationDTO.setJurisdiction("invalid path jurisdiction");
        given(commentHeaderConfig.getJurisdictionPaths()).willReturn(jurisdictionPaths);
        String commentHeader = ccdService.buildCommentHeader(annotationDTO, jwt);
        assertNull(commentHeader);
        verify(coreCaseDataApi).getCase(jwt, serviceToken, caseId);
    }

    @Test
    void buildCommentHeader() {
        given(authTokenGenerator.generate()).willReturn(serviceToken);
        given(coreCaseDataApi.getCase(jwt, serviceToken, caseId)).willReturn(caseDetails);
        given(commentHeaderConfig.getJurisdictionPaths()).willReturn(jurisdictionPaths);
        String commentHeader = ccdService.buildCommentHeader(annotationDTO, jwt);
        assertEquals("Value1 Value21", commentHeader);
        verify(coreCaseDataApi).getCase(jwt, serviceToken, caseId);
    }

}
