package uk.gov.hmcts.reform.em.annotation.functional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;

import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.API_ANNOTATION_SETS;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.CONTENT_TYPE_JSON;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.STATUS_UNAUTHORIZED;

class OpenIdConnectScenariosTest extends BaseTest {

    @Autowired
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
