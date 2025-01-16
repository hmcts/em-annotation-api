package uk.gov.hmcts.reform.em.annotation.functional;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class OpenIdConnectScenariosTest extends BaseTest {

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
