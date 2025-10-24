package uk.gov.hmcts.reform.em.annotation.functional;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.API_ANNOTATION_SETS;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.API_FILTER;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.FIELD_DOCUMENT_ID;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.FIELD_ID;

class FilterAnnotationSetScenariosTest extends BaseTest {

    @Autowired
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
