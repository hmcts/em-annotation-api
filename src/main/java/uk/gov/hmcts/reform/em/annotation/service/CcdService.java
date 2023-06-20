package uk.gov.hmcts.reform.em.annotation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.*;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;

import java.util.Objects;

@Service
public class CcdService {

    private final Logger log = LoggerFactory.getLogger(CcdService.class);
    private final CoreCaseDataApi coreCaseDataApi;

    private final AuthTokenGenerator authTokenGenerator;

    public CcdService(
            CoreCaseDataApi coreCaseDataApi,
            AuthTokenGenerator authTokenGenerator
    ) {
        this.coreCaseDataApi = coreCaseDataApi;
        this.authTokenGenerator = authTokenGenerator;
    }

    public AnnotationDTO fetchAppellantDetails(AnnotationDTO annotationDTO, String authorisation) {
        if (Objects.isNull(annotationDTO.getCaseId())) {
            return annotationDTO;
        }
        if (Objects.nonNull(annotationDTO.getAppellant())) {
            return annotationDTO;
        }
        if (!(annotationDTO.getJurisdiction().equals("SSCS") || annotationDTO.getJurisdiction().equals("IA"))) {
            return annotationDTO;
        }
        CaseDetails caseDetails = getCaseDetails(authorisation, authTokenGenerator.generate(),"1584612819251434");
        caseDetails.getData();
        annotationDTO.setAppellant("");
        return annotationDTO;
    }

    public CaseDetails getCaseDetails(String authorisation, String serviceAuthorisation, String caseId) {
        String serviceAuth = authTokenGenerator.generate();
        return coreCaseDataApi.getCase(authorisation,
                serviceAuth, "1584612819251434");
    }
}


