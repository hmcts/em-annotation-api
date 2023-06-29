package uk.gov.hmcts.reform.em.annotation.service;

import com.jayway.jsonpath.JsonPath;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.*;
import uk.gov.hmcts.reform.em.annotation.config.CommentHeaderConfig;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Service
public class CcdService {

    private final Logger log = LoggerFactory.getLogger(CcdService.class);

    private final CommentHeaderConfig commentHeaderConfig;

    private final CoreCaseDataApi coreCaseDataApi;

    private final AuthTokenGenerator authTokenGenerator;

    public CcdService(
            CoreCaseDataApi coreCaseDataApi,
            AuthTokenGenerator authTokenGenerator,
            CommentHeaderConfig commentHeaderConfig
    ) {
        this.coreCaseDataApi = coreCaseDataApi;
        this.authTokenGenerator = authTokenGenerator;
        this.commentHeaderConfig = commentHeaderConfig;
    }

    public String fetchAppellantDetails(AnnotationDTO annotationDTO, String authorisation) {
        if (Objects.isNull(annotationDTO.getCaseId())) {
            return annotationDTO.getCommentHeader();
        }
        if (Objects.nonNull(annotationDTO.getCommentHeader())) {
            return annotationDTO.getCommentHeader();
        }
        HashMap<String, ArrayList<String>> jurisdictionPaths = commentHeaderConfig.getJurisdictionPaths();

        if (!jurisdictionPaths.containsKey(annotationDTO.getJurisdiction())) {
            return annotationDTO.getCommentHeader();
        }
        CaseDetails caseDetails = getCaseDetails(authorisation, annotationDTO.getCaseId());
        JSONObject jsonObject = new JSONObject(caseDetails.getData());
        ArrayList<String> paths = jurisdictionPaths.get(annotationDTO.getJurisdiction());

        StringBuilder stringBuilder = new StringBuilder();
        for (String path : paths) {
            stringBuilder.append(" ");
            stringBuilder.append(JsonPath.read(jsonObject.toString(), path).toString());
        }

        return stringBuilder.toString().trim();
    }

    public CaseDetails getCaseDetails(String authorisation, String caseId) {
        String serviceAuth = authTokenGenerator.generate();
        CaseDetails caseDetails = coreCaseDataApi.getCase(authorisation,
                serviceAuth, caseId);
        log.info("caseDetails value is {}", caseDetails);
        log.info("caseDetails.data value is {}", caseDetails.getData());
        return caseDetails;

    }
}


