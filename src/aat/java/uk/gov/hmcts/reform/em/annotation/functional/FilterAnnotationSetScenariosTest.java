package uk.gov.hmcts.reform.em.annotation.functional;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

class FilterAnnotationSetScenariosTest extends BaseTest {

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
