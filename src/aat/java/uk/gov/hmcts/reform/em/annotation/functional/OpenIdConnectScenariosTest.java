package uk.gov.hmcts.reform.em.annotation.functional;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;

class OpenIdConnectScenariosTest extends BaseTest {

    private static final String API_ANNOTATION_SETS = "/api/annotation-sets";

    private static final String CONTENT_TYPE_JSON = MediaType.APPLICATION_JSON_VALUE;

    private static final int STATUS_UNAUTHORIZED = 401;

    public OpenIdConnectScenariosTest(TestUtil testUtil) {
        super(testUtil);
    }

    @Test
    void testWithInvalidIdamAuth() {
        // Invalid IdamAuth
        testUtil
                .invalidIdamAuthrequest()
                .baseUri(testUrl)
                .contentType(CONTENT_TYPE_JSON)
                .get(API_ANNOTATION_SETS)
                .then()
                .statusCode(STATUS_UNAUTHORIZED);
    }

    @Test
    void testWithEmptyS2SAuth() {
        // Empty S2SAuth
        testUtil
                .validAuthRequestWithEmptyS2SAuth()
                .baseUri(testUrl)
                .contentType(CONTENT_TYPE_JSON)
                .get(API_ANNOTATION_SETS)
                .then()
                .statusCode(STATUS_UNAUTHORIZED);
    }

    @Test
    void testWithEmptyIdamAuthAndValidS2SAuth() {
        // Empty IdamAuth and Valid S2S Auth
        testUtil
                .validS2SAuthWithEmptyIdamAuth()
                .baseUri(testUrl)
                .contentType(CONTENT_TYPE_JSON)
                .get(API_ANNOTATION_SETS)
                .then()
                .statusCode(STATUS_UNAUTHORIZED);
    }

    @Test
    void testIdamAuthAndS2SAuthAreEmpty() {
        // Empty IdamAuth and Empty S2SAuth
        testUtil
                .emptyIdamAuthAndEmptyS2SAuth()
                .baseUri(testUrl)
                .contentType(CONTENT_TYPE_JSON)
                .get(API_ANNOTATION_SETS)
                .then()
                .statusCode(STATUS_UNAUTHORIZED);
    }
}
