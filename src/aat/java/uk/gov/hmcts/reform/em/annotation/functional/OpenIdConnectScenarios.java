package uk.gov.hmcts.reform.em.annotation.functional;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;

@SpringBootTest(classes = {TestUtil.class, EmTestConfig.class})
@TestPropertySource(value = "classpath:application.yml")
@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
public class OpenIdConnectScenarios {

    @Rule
    public ExpectedException exceptionThrown = ExpectedException.none();

    @Autowired
    private TestUtil testUtil;

    @Value("${test.url}")
    private String testUrl;

    @Test
    // Invalid IdamAuth
    public void testWithInvalidIdamAuth() {
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
    public void testWithEmptyS2SAuth() {
        exceptionThrown.expect(NullPointerException.class);

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
    public void testWithEmptyIdamAuthAndValidS2SAuth() {
        exceptionThrown.expect(NullPointerException.class);

        testUtil
                .validS2SAuthWithEmptyIdamAuth()
                .baseUri(testUrl)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("/api/annotation-sets");

        exceptionThrown.expectMessage("Header value cannot be null");
    }

    @Test
    // Empty IdamAuth and Empty S2SAuth
    public void testIdamAuthAndS2SAuthAreEmpty() {
        exceptionThrown.expect(NullPointerException.class);

        testUtil
                .emptyIdamAuthAndEmptyS2SAuth()
                .baseUri(testUrl)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("/api/annotation-sets");
    }

}
