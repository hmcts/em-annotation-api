package uk.gov.hmcts.reform.em.annotation.functional;

import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;
import uk.gov.hmcts.reform.em.test.retry.RetryRule;

@SpringBootTest(classes = {TestUtil.class})
@TestPropertySource(value = "classpath:application.yml")
@ExtendWith({SerenityJUnit5Extension.class, SpringExtension.class})
@WithTags({@WithTag("testType:Functional")})
class OpenIdConnectScenariosTest {

    @Autowired
    private TestUtil testUtil;

    @Value("${test.url}")
    private String testUrl;

    @Rule
    public RetryRule retryRule = new RetryRule(3);

    @Test
    // Invalid IdamAuth
    void testWithInvalidIdamAuth() {
        testUtil
                .invalidIdamAuthrequest()
                .baseUri(testUrl)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("/api/annotation-sets")
                .then()
                .statusCode(401);
    }

    @Test
    // Empty S2SAuth
    void testWithEmptyS2SAuth() {

        testUtil
                .validAuthRequestWithEmptyS2SAuth()
                .baseUri(testUrl)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("/api/annotation-sets")
                .then()
                .statusCode(401);
    }

    @Test
    // Empty IdamAuth and Valid S2S Auth
    void testWithEmptyIdamAuthAndValidS2SAuth() {

        testUtil
            .validS2SAuthWithEmptyIdamAuth()
            .baseUri(testUrl)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .get("/api/annotation-sets")
            .then()
            .statusCode(401);

    }

    @Test
    // Empty IdamAuth and Empty S2SAuth
    void testIdamAuthAndS2SAuthAreEmpty() {

        testUtil
            .emptyIdamAuthAndEmptyS2SAuth()
            .baseUri(testUrl)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .get("/api/annotation-sets")
            .then()
            .statusCode(401);
    }

}
