package uk.gov.hmcts.reform.em.annotation.testutil;

import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.test.ccddata.CcdDataHelper;
import uk.gov.hmcts.reform.em.test.idam.IdamHelper;
import uk.gov.hmcts.reform.em.test.s2s.S2sHelper;

import java.util.stream.Stream;

import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.ANNOTATION_TEST_USER_EMAIL;

@Service
@ComponentScan({"uk.gov.hmcts.reform.em.test.idam",
    "uk.gov.hmcts.reform.em.test.s2s",
    "uk.gov.hmcts.reform.em.test.ccddata"})
@EnableAutoConfiguration
public class TestUtil {

    public static final String AUTHORIZATION = "Authorization";
    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    private final IdamHelper idamHelper;

    private final S2sHelper s2sHelper;

    private final CcdDataHelper ccdDataHelper;

    private String idamAuth;
    private String s2sAuth;

    @Autowired
    public TestUtil(IdamHelper idamHelper, S2sHelper s2sHelper, CcdDataHelper ccdDataHelper) {
        this.idamHelper = idamHelper;
        this.s2sHelper = s2sHelper;
        this.ccdDataHelper = ccdDataHelper;
    }

    @PostConstruct
    void postConstruct() {
        SerenityRest.useRelaxedHTTPSValidation();
        idamHelper
                .createUser(ANNOTATION_TEST_USER_EMAIL,
                        Stream.of("caseworker", "caseworker-publiclaw").toList());
        idamAuth = idamHelper.authenticateUser(ANNOTATION_TEST_USER_EMAIL);
        s2sAuth = s2sHelper.getS2sToken();
    }

    @PreDestroy
    void preDestroy() {
        idamHelper.deleteUser(ANNOTATION_TEST_USER_EMAIL);
    }

    public RequestSpecification authRequest() {
        return SerenityRest
                .given()
                .header(AUTHORIZATION, idamHelper.authenticateUser(ANNOTATION_TEST_USER_EMAIL))
                .header(SERVICE_AUTHORIZATION, s2sHelper.getS2sToken());
    }

    public RequestSpecification unauthenticatedRequest() {
        return SerenityRest.given();
    }

    public RequestSpecification emptyIdamAuthRequest() {
        return s2sAuthRequest()
                .header(new Header(AUTHORIZATION, null));
    }

    public RequestSpecification emptyIdamAuthAndEmptyS2SAuth() {
        return SerenityRest
                .given()
                .header(new Header(SERVICE_AUTHORIZATION, null))
                .header(new Header(AUTHORIZATION, null));
    }

    public RequestSpecification validAuthRequestWithEmptyS2SAuth() {
        return emptyS2sAuthRequest().header(AUTHORIZATION, idamAuth);
    }

    public RequestSpecification validS2SAuthWithEmptyIdamAuth() {

        return s2sAuthRequest().header(new Header(AUTHORIZATION, null));
    }

    private RequestSpecification emptyS2sAuthRequest() {

        return SerenityRest.given().header(new Header(SERVICE_AUTHORIZATION, null));
    }

    public RequestSpecification invalidIdamAuthrequest() {

        return s2sAuthRequest().header(AUTHORIZATION, "invalidIDAMAuthRequest");
    }

    public RequestSpecification invalidS2SAuth() {

        return invalidS2sAuthRequest().header(AUTHORIZATION, idamAuth);
    }

    private RequestSpecification invalidS2sAuthRequest() {

        return SerenityRest.given().header(SERVICE_AUTHORIZATION, "invalidS2SAuthorization");
    }

    private RequestSpecification s2sAuthRequest() {
        return SerenityRest
                .given()
                .header(SERVICE_AUTHORIZATION, s2sAuth);
    }

    public CaseDetails createCase(String jurisdiction, String caseType, Object data) {
        return ccdDataHelper.createCase(ANNOTATION_TEST_USER_EMAIL, jurisdiction, caseType, "createCase", data);
    }

}
