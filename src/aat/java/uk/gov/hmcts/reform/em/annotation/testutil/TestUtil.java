package uk.gov.hmcts.reform.em.annotation.testutil;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.em.test.idam.IdamHelper;
import uk.gov.hmcts.reform.em.test.s2s.S2sHelper;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TestUtil {

    @Autowired
    private IdamHelper idamHelper;

    @Autowired
    private S2sHelper s2sHelper;

    @Value("${test.url}")
    private String testUrl;
    @Value("${idam.api.url}")
    private String idamAPIUrl;
    @Value("${idam.client.id}")
    private String idamClientId;
    @Value("${idam.client.secret}")
    private String idamClientSecret;
    @Value("${idam.client.redirect_uri}")
    private String idamClientRedirectUrl;

    @PostConstruct
    void postConstruct() {
        printAllEnv();
        RestAssured.useRelaxedHTTPSValidation();
        idamHelper.createUser("a@b.com", Stream.of("caseworker").collect(Collectors.toList()));
    }

    public RequestSpecification authRequest() {
        return RestAssured
            .given()
            .header("Authorization", idamHelper.authenticateUser("a@b.com"))
            .header("ServiceAuthorization", s2sHelper.getS2sToken());
    }

    private void printAllEnv() {
        System.out.println(String.format("SYS_VARS"));
        System.out.println(String.format("testUrl=%s", testUrl));
        System.out.println(String.format("idamAPIUrl=%s", idamAPIUrl));
        System.out.println(String.format("idamClientId=%s", idamClientId));
        System.out.println(String.format("idamClientSecret=%s", idamClientSecret));
        System.out.println(String.format("idamClientRedirectUrl=%s", idamClientRedirectUrl));
    }
}
