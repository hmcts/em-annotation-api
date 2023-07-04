package uk.gov.hmcts.reform.em.annotation.testutil;

import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.test.ccddata.CcdDataHelper;
import uk.gov.hmcts.reform.em.test.idam.IdamHelper;
import uk.gov.hmcts.reform.em.test.s2s.S2sHelper;

import java.util.UUID;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;

@Service
@ComponentScan({"uk.gov.hmcts.reform.em.test.idam", "uk.gov.hmcts.reform.em.test.s2s"})
@EnableAutoConfiguration
public class TestUtil {

    @Autowired
    private IdamHelper idamHelper;

    @Autowired
    private S2sHelper s2sHelper;

    @Autowired
    private CcdDataHelper ccdDataHelper;

    private String idamAuth;
    private String s2sAuth;

    private UUID documentId = UUID.randomUUID();

    private final String username = "em5316testuser@mailinator.com";

    @PostConstruct
    void postConstruct() {
        SerenityRest.useRelaxedHTTPSValidation();
        //idamHelper.createUser(username, Stream.of("caseworker", "caseworker-publiclaw").collect(Collectors.toList()));
        idamAuth = idamHelper.authenticateUser(username);
        s2sAuth = s2sHelper.getS2sToken();
    }

    public RequestSpecification authRequest() {
        return SerenityRest
                .given()
                .header("Authorization", idamHelper.authenticateUser("em5316testuser@mailinator.com"))
                .header("ServiceAuthorization", s2sHelper.getS2sToken());
    }

    public RequestSpecification unauthenticatedRequest() {
        return SerenityRest.given();
    }

    public RequestSpecification emptyIdamAuthRequest() {
        return s2sAuthRequest()
                .header("Authorization", null);
    }

    public RequestSpecification emptyIdamAuthAndEmptyS2SAuth() {
        return SerenityRest
                .given()
                .header("ServiceAuthorization", null)
                .header("Authorization", null);
    }

    public RequestSpecification validAuthRequestWithEmptyS2SAuth() {
        return emptyS2sAuthRequest().header("Authorization", idamAuth);
    }

    public RequestSpecification validS2SAuthWithEmptyIdamAuth() {

        return s2sAuthRequest().header("Authorization", null);
    }

    private RequestSpecification emptyS2sAuthRequest() {

        return SerenityRest.given().header("ServiceAuthorization", null);
    }

    public RequestSpecification invalidIdamAuthrequest() {

        return s2sAuthRequest().header("Authorization", "invalidIDAMAuthRequest");
    }

    public RequestSpecification invalidS2SAuth() {

        return invalidS2sAuthRequest().header("Authorization", idamAuth);
    }

    private RequestSpecification invalidS2sAuthRequest() {

        return SerenityRest.given().header("ServiceAuthorization", "invalidS2SAuthorization");
    }

    private RequestSpecification s2sAuthRequest() {
        return SerenityRest
                .given()
                .header("ServiceAuthorization", s2sAuth);
    }

    public CaseDetails createCase(String jurisdiction, String caseType, Object data) {
        return ccdDataHelper.createCase(username, jurisdiction, caseType, "createCase", data);
    }

}
