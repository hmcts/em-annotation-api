package uk.gov.hmcts.reform.em.annotation.service;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.annotation.config.CommentHeaderConfig;
import uk.gov.hmcts.reform.em.annotation.service.dto.AnnotationDTO;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CcdService {

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

    public String buildCommentHeader(AnnotationDTO annotationDTO, String authorisation) {
        Map<String, List<String>> jurisdictionPaths = commentHeaderConfig.getJurisdictionPaths();

        if (useExistingCommentHeader(annotationDTO, jurisdictionPaths)) {
            return annotationDTO.getCommentHeader();
        }

        CaseDetails caseDetails = getCaseDetails(authorisation, annotationDTO.getCaseId());
        JSONObject jsonObject = new JSONObject(caseDetails.getData());
        List<String> paths = jurisdictionPaths.get(annotationDTO.getJurisdiction());

        String commentHeader = buildCommentHeaderString(jsonObject, paths);
        if (commentHeader.isEmpty()) {
            return annotationDTO.getCommentHeader();
        }
        return commentHeader;
    }

    private String buildCommentHeaderString(JSONObject jsonObject, List<String> paths) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String path : paths) {
            try {
                stringBuilder.append(JsonPath.read(jsonObject.toString(), path).toString());
                stringBuilder.append(" ");
            } catch (PathNotFoundException ignored) {
                //ignored
            }
        }
        return stringBuilder.toString().trim();
    }

    private boolean useExistingCommentHeader(AnnotationDTO annotationDTO,
                                             Map<String, List<String>> jurisdictionPaths) {
        if (Objects.isNull(annotationDTO.getCaseId())) {
            return true;
        }
        if (Objects.nonNull(annotationDTO.getCommentHeader())) {
            return true;
        }
        return !jurisdictionPaths.containsKey(annotationDTO.getJurisdiction());
    }

    protected CaseDetails getCaseDetails(String authorisation, String caseId) {
        String serviceAuth = authTokenGenerator.generate();
        return coreCaseDataApi.getCase(authorisation,
                serviceAuth, caseId);
    }
}
