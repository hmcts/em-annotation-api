package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.specification.RequestSpecification;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;
import uk.gov.hmcts.reform.em.test.retry.RetryRule;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = {TestUtil.class})
@TestPropertySource(value = "classpath:application.yml")
@ExtendWith({SerenityJUnit5Extension.class, SpringExtension.class})
@WithTags({@WithTag("testType:Functional")})
class FilterAnnotationSetScenariosTest {

    @Autowired
    private TestUtil testUtil;

    @Value("${test.url}")
    private String testUrl;

    @Rule
    public RetryRule retryRule = new RetryRule(3);

    private RequestSpecification request;
    private RequestSpecification unAuthenticatedRequest;

    @BeforeEach
    public void setupRequestSpecification() {
        request = testUtil
                .authRequest()
                .baseUri(testUrl)
                .contentType(APPLICATION_JSON_VALUE);

        unAuthenticatedRequest = testUtil
                .unauthenticatedRequest()
                .baseUri(testUrl)
                .contentType(APPLICATION_JSON_VALUE);
    }

    @Test
    void shouldReturn404WhenFilterAnnotationSetWithNonExistentDocumentId() {
        final UUID documentId = UUID.randomUUID();

        request
                .param("documentId", documentId)
                .get("/api/annotation-sets/filter")
                .then()
                .assertThat()
                .statusCode(204)
                .log().all();
    }

    @Test
    void shouldReturn200WhenFilterAnnotationSetWithDocumentId() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        createAnnotationSet(annotationSetId, documentId);

        request
                .param("documentId", documentId)
                .get("/api/annotation-sets/filter")
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo(annotationSetId.toString()))
                .body("documentId", equalTo(documentId.toString()))
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserFilterAnnotationSetWithDocumentId() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        createAnnotationSet(annotationSetId, documentId);

        unAuthenticatedRequest
                .param("documentId", documentId)
                .get("/api/annotation-sets/filter")
                .then()
                .assertThat()
                .statusCode(401)
                .log().all();
    }

    private void createAnnotationSet(final UUID annotationSetId, final UUID documentId) {
        final JSONObject annotationSet = createAnnotationSetPayload(annotationSetId, documentId);
        request
                .body(annotationSet.toString())
                .post("/api/annotation-sets")
                .then()
                .assertThat()
                .statusCode(201)
                .body("id", equalTo(annotationSetId.toString()))
                .body("documentId", equalTo(documentId.toString()))
                .header("Location", equalTo("/api/annotation-sets/" + annotationSetId))
                .log().all();
    }

    private JSONObject createAnnotationSetPayload(final UUID annotationSetId, final UUID documentId) {
        final JSONObject annotationSet = new JSONObject();
        annotationSet.put("documentId", documentId);
        annotationSet.put("id", annotationSetId.toString());

        return annotationSet;
    }
}
