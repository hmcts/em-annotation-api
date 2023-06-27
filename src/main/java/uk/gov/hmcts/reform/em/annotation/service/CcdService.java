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
import java.util.Map;
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
        Map appeal = (Map<String, Map<String, String>>) caseDetails.getData().get("appeal");
        Map<String, Map<String, String>> appellant = (Map<String, Map<String, String>>) appeal.get("appellant");
        Map<String, String> name = appellant.get("name");

        String appellantStr = (name.values()
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(" ")));

        JSONObject jsonObject = new JSONObject(caseDetails.getData());
        LinkedHashMap<String, String> appellantName =  JsonPath.read(jsonObject.toString(), "appeal.appellant.name");
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


