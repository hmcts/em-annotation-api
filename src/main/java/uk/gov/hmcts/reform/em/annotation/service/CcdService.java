package uk.gov.hmcts.reform.em.annotation.service;

import com.jayway.jsonpath.JsonPath;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.*;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public String fetchAppellantDetails(AnnotationDTO annotationDTO, String authorisation) {
        if (Objects.isNull(annotationDTO.getCaseId())) {
            return annotationDTO.getAppellant();
        }
        if (Objects.nonNull(annotationDTO.getAppellant())) {
            return annotationDTO.getAppellant();
        }
        if (!(annotationDTO.getJurisdiction().equals("SSCS") || annotationDTO.getJurisdiction().equals("IA"))) {
            return annotationDTO.getAppellant();
        }
        CaseDetails caseDetails = getCaseDetails(authorisation, authTokenGenerator.generate(), annotationDTO.getCaseId());

        JSONObject jsonObject = new JSONObject(caseDetails.getData());
        if (annotationDTO.getJurisdiction().equals("IA")) {
            return JsonPath.read(jsonObject.toString(), "appellantnamefordisplay").toString();
        }
        String jsonPath = "appeal.appellant.name";
        LinkedHashMap<String, String> appellantName =  JsonPath.read(jsonObject.toString(), jsonPath);
        return appellantName.values()
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "));


    }

    public CaseDetails getCaseDetails(String authorisation, String serviceAuthorisation, String caseId) {
        String serviceAuth = authTokenGenerator.generate();
        CaseDetails caseDetails = coreCaseDataApi.getCase(authorisation,
                serviceAuth, caseId);
        log.info("caseDetails value is {}", caseDetails);
        log.info("caseDetails.data value is {}", caseDetails.getData());
        return caseDetails;

    }
}


