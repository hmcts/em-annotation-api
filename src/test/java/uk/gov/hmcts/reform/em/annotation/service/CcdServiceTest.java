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

    private final String caseId = "test_case_id";
    private final String jwt = "jtw_test";
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
    void should_startCcdEvent() {
        given(authTokenGenerator.generate()).willReturn(serviceToken);
        given(coreCaseDataApi.getCase(jwt, serviceToken, caseId)).willReturn(caseDetails);
        ccdService.getCaseDetails(jwt, caseId);
        verify(coreCaseDataApi).getCase(jwt, serviceToken, caseId);
    }

    @Test
    void should_return_default_with_null_caseId() {
        annotationDTO.setCaseId(null);
        String commentHeader = ccdService.fetchAppellantDetails(annotationDTO, jwt);
        assertNull(commentHeader);
        verifyNoInteractions(coreCaseDataApi);
    }

    @Test
    void should_return_default_with_nonNull_commentHeader() {
        annotationDTO.setCommentHeader("commentHeader");
        String commentHeader = ccdService.fetchAppellantDetails(annotationDTO, jwt);
        assertEquals("commentHeader", commentHeader);
        verifyNoInteractions(coreCaseDataApi);
    }

    @Test
    void should_return_default_with_invalidJurisdiction() {
        annotationDTO.setJurisdiction("invalid jurisdiction");
        given(commentHeaderConfig.getJurisdictionPaths()).willReturn(jurisdictionPaths);
        String commentHeader = ccdService.fetchAppellantDetails(annotationDTO, jwt);
        assertNull(commentHeader);
        verifyNoInteractions(coreCaseDataApi);
    }

    @Test
    void should_return_default_with_invalid_path() {
        given(authTokenGenerator.generate()).willReturn(serviceToken);
        given(coreCaseDataApi.getCase(jwt, serviceToken, caseId)).willReturn(caseDetails);
        annotationDTO.setJurisdiction("invalid path jurisdiction");
        given(commentHeaderConfig.getJurisdictionPaths()).willReturn(jurisdictionPaths);
        String commentHeader = ccdService.fetchAppellantDetails(annotationDTO, jwt);
        assertNull(commentHeader);
        verify(coreCaseDataApi).getCase(jwt, serviceToken, caseId);
    }

    @Test
    void should_return_new_header_with_valid_path() {
        given(authTokenGenerator.generate()).willReturn(serviceToken);
        given(coreCaseDataApi.getCase(jwt, serviceToken, caseId)).willReturn(caseDetails);
        given(commentHeaderConfig.getJurisdictionPaths()).willReturn(jurisdictionPaths);
        String commentHeader = ccdService.fetchAppellantDetails(annotationDTO, jwt);
        assertEquals("Value1 Value21", commentHeader);
        verify(coreCaseDataApi).getCase(jwt, serviceToken, caseId);
    }

}
