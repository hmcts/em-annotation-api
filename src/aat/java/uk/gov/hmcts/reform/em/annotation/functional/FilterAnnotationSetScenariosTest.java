package uk.gov.hmcts.reform.em.annotation.functional;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

class FilterAnnotationSetScenariosTest extends BaseTest {

    // Constants to avoid string duplication
    private static final String API_ANNOTATION_SETS = "/api/annotation-sets";
    private static final String API_FILTER = API_ANNOTATION_SETS + "/filter";
    private static final String FIELD_DOCUMENT_ID = "documentId";
    private static final String FIELD_ID = "id";

    public FilterAnnotationSetScenariosTest(TestUtil testUtil) {
        super(testUtil);
    }

    @Test
    void shouldReturn404WhenFilterAnnotationSetWithNonExistentDocumentId() {
        final UUID documentId = UUID.randomUUID();

        request
                .param(FIELD_DOCUMENT_ID, documentId)
                .get(API_FILTER)
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
                .param(FIELD_DOCUMENT_ID, documentId)
                .get(API_FILTER)
                .then()
                .assertThat()
                .statusCode(200)
                .body(FIELD_ID, equalTo(annotationSetId.toString()))
                .body(FIELD_DOCUMENT_ID, equalTo(documentId.toString()))
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserFilterAnnotationSetWithDocumentId() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        createAnnotationSet(annotationSetId, documentId);

        unAuthenticatedRequest
                .param(FIELD_DOCUMENT_ID, documentId)
                .get(API_FILTER)
                .then()
                .assertThat()
                .statusCode(401)
                .log().all();
    }

    private void createAnnotationSet(final UUID annotationSetId, final UUID documentId) {
        final JSONObject annotationSet = createAnnotationSetPayload(annotationSetId, documentId);
        request
                .body(annotationSet.toString())
                .post(API_ANNOTATION_SETS)
                .then()
                .assertThat()
                .statusCode(201)
                .body(FIELD_ID, equalTo(annotationSetId.toString()))
                .body(FIELD_DOCUMENT_ID, equalTo(documentId.toString()))
                .header("Location", equalTo(API_ANNOTATION_SETS + "/" + annotationSetId))
                .log().all();
    }

    private JSONObject createAnnotationSetPayload(final UUID annotationSetId, final UUID documentId) {
        final JSONObject annotationSet = new JSONObject();
        annotationSet.put(FIELD_DOCUMENT_ID, documentId);
        annotationSet.put(FIELD_ID, annotationSetId.toString());
        return annotationSet;
    }
}
