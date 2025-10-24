package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.API_ANNOTATIONS;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.API_ANNOTATION_SETS;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.FIELD_ANNOTATIONS;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.FIELD_ANNOTATION_ID;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.FIELD_ANNOTATION_SET_ID;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.FIELD_ANNOTATION_TYPE;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.FIELD_COLOR;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.FIELD_COMMENTS;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.FIELD_CONTENT;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.FIELD_DOCUMENT_ID;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.FIELD_ID;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.FIELD_PAGE;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.FIELD_RECTANGLES;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.HEADER_LOCATION;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.VALUE_COLOR;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.VALUE_HIGHLIGHT;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.VALUE_TEXT;

class AnnotationSetScenariosTest extends BaseTest {

    @Autowired
    public AnnotationSetScenariosTest(TestUtil testUtil) {
        super(testUtil);
    }

    @Test
    void shouldReturn201WhenCreateNewAnnotationSet() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        final ValidatableResponse response = createAnnotationSet(annotationSetId, documentId);

        response
                .statusCode(201)
                .body(FIELD_DOCUMENT_ID, equalTo(documentId.toString()))
                .body(FIELD_ID, equalTo(annotationSetId.toString()))
                .header(HEADER_LOCATION, equalTo(API_ANNOTATION_SETS + "/" + annotationSetId))
                .log().all();
    }

    @Test
    void shouldReturn400WhenCreateNewAnnotationSetWithoutId() {
        final JSONObject annotationSet = new JSONObject();
        final UUID documentId = UUID.randomUUID();
        annotationSet.put(FIELD_DOCUMENT_ID, documentId);

        request
                .body(annotationSet.toString())
                .post(API_ANNOTATION_SETS)
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserCreateNewAnnotationSet() {
        final JSONObject annotationSet = new JSONObject();
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        annotationSet.put(FIELD_DOCUMENT_ID, documentId);
        annotationSet.put(FIELD_ID, annotationSetId.toString());

        unAuthenticatedRequest
                .body(annotationSet.toString())
                .post(API_ANNOTATION_SETS)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenGetAnnotationSetById() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        final ValidatableResponse response = createAnnotationSet(annotationSetId, documentId);
        final JSONObject annotationSet = extractJsonObjectFromResponse(response);
        final String id = annotationSet.getString(FIELD_ID);

        request
                .get(API_ANNOTATION_SETS + "/" + id)
                .then()
                .statusCode(200)
                .body(FIELD_ID, equalTo(annotationSetId.toString()))
                .body(FIELD_DOCUMENT_ID, is(documentId.toString()))
                .log().all();
    }

    @Test
    void shouldReturn204WhenGetAnnotationSetNotFoundById() {
        final String annotationSetId = UUID.randomUUID().toString();

        request
                .get(API_ANNOTATION_SETS + "/" + annotationSetId)
                .then()
                .statusCode(204)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserGetAnnotationSetById() {
        final String annotationSetId = UUID.randomUUID().toString();

        unAuthenticatedRequest
                .get(API_ANNOTATION_SETS + "/" + annotationSetId)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenGetAllAnnotationSets() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        createAnnotationSet(annotationSetId, documentId);

        request
                .get(API_ANNOTATION_SETS)
                .then()
                .statusCode(200)
                .body("size()", Matchers.greaterThanOrEqualTo(1))
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserGetAllAnnotationSets() {
        unAuthenticatedRequest
                .get(API_ANNOTATION_SETS)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenUpdateAnnotationSet() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        final ValidatableResponse response = createAnnotationSet(annotationSetId, documentId);
        final JSONObject annotationSet = extractJsonObjectFromResponse(response);
        final UUID newDocumentId = UUID.randomUUID();
        annotationSet.put(FIELD_DOCUMENT_ID, newDocumentId);

        request
                .body(annotationSet.toString())
                .put(API_ANNOTATION_SETS)
                .then()
                .statusCode(200)
                .body(FIELD_ID, equalTo(annotationSetId.toString()))
                .body(FIELD_DOCUMENT_ID, is(newDocumentId.toString()))
                .log().all();
    }

    @Test
    void shouldReturn200WhenUpdateAnnotationSetWithNewAnnotation() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        final ValidatableResponse response = createAnnotationSet(annotationSetId, documentId);
        final JSONObject annotationSet = extractJsonObjectFromResponse(response);
        final UUID newDocumentId = UUID.randomUUID();
        annotationSet.put(FIELD_DOCUMENT_ID, newDocumentId);
        JSONArray annotations = annotationSet.getJSONArray(FIELD_ANNOTATIONS);
        annotations.put(1, extractJsonObjectFromResponse(
                createAnnotation(UUID.randomUUID().toString(), annotationSetId.toString())));
        annotationSet.put(FIELD_ANNOTATIONS, annotations);

        request
                .body(annotationSet.toString())
                .put(API_ANNOTATION_SETS)
                .then()
                .statusCode(200)
                .body(FIELD_ID, equalTo(annotationSetId.toString()))
                .body(FIELD_DOCUMENT_ID, is(newDocumentId.toString()))
                .body(FIELD_ANNOTATIONS, hasSize(2))
                .log().all();
    }

    @Test
    void shouldReturn400WhenUpdateAnnotationSetWithoutId() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        final ValidatableResponse response = createAnnotationSet(annotationSetId, documentId);
        final JSONObject annotationSet = extractJsonObjectFromResponse(response);
        annotationSet.remove(FIELD_ID);

        request
                .body(annotationSet.toString())
                .put(API_ANNOTATION_SETS)
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserUpdateAnnotationSet() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        final ValidatableResponse response = createAnnotationSet(annotationSetId, documentId);
        final JSONObject annotationSet = extractJsonObjectFromResponse(response);
        final UUID newDocumentId = UUID.randomUUID();
        annotationSet.put(FIELD_DOCUMENT_ID, newDocumentId);

        unAuthenticatedRequest
                .body(annotationSet.toString())
                .put(API_ANNOTATION_SETS)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenDeleteAnnotationSetById() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        final ValidatableResponse response = createAnnotationSet(annotationSetId, documentId);
        final String id = extractJsonObjectFromResponse(response).getString(FIELD_ID);
        final ValidatableResponse deletedResponse = deleteAnnotationSetById(id);

        deletedResponse.statusCode(200);
    }

    @Test
    void shouldReturn200WhenDeleteAnnotationSetByNonExistentId() {
        final String nonExistentAnnotationSetId = UUID.randomUUID().toString();
        final ValidatableResponse deletedResponse = deleteAnnotationSetById(nonExistentAnnotationSetId);

        deletedResponse.statusCode(200);
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserDeleteAnnotationSet() {
        unAuthenticatedRequest
                .delete(API_ANNOTATION_SETS + "/" + UUID.randomUUID())
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenUpdateAnnotationSetAfterItHasBeenDeleted() {
        final UUID annotationSetId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        final UUID newDocumentId = UUID.randomUUID();
        final ValidatableResponse response = createAnnotationSet(annotationSetId, documentId);
        final String id = extractJsonObjectFromResponse(response).getString(FIELD_ID);
        final JSONObject annotationSet = extractJsonObjectFromResponse(response);
        deleteAnnotationSetById(id).statusCode(200);

        annotationSet.put(FIELD_DOCUMENT_ID, newDocumentId);

        request
                .body(annotationSet.toString())
                .put(API_ANNOTATION_SETS)
                .then()
                .statusCode(200)
                .body(FIELD_ID, equalTo(annotationSetId.toString()))
                .body(FIELD_DOCUMENT_ID, is(newDocumentId.toString()))
                .log().all();
    }

    // ===== Helper methods =====

    @NotNull
    private ValidatableResponse deleteAnnotationSetById(String annotationSetId) {
        return request
                .delete(API_ANNOTATION_SETS + "/" + annotationSetId)
                .then()
                .log().all();
    }

    @NotNull
    private ValidatableResponse createAnnotationSet(final UUID annotationSetId, final UUID documentId) {
        final JSONObject annotationSet = createAnnotationSetPayload(annotationSetId, documentId);
        return request
                .body(annotationSet.toString())
                .post(API_ANNOTATION_SETS)
                .then()
                .statusCode(201)
                .log().all();
    }

    @NotNull
    private JSONObject createAnnotationSetPayload(final UUID annotationSetId, final UUID documentId) {
        final JSONObject annotationSet = new JSONObject();
        annotationSet.put(FIELD_DOCUMENT_ID, documentId);
        annotationSet.put(FIELD_ID, annotationSetId.toString());
        final JSONArray annotations = new JSONArray();
        final JSONObject annotation = extractJsonObjectFromResponse(
                createAnnotation(UUID.randomUUID().toString(), annotationSetId.toString()));
        annotations.put(0, annotation);
        annotationSet.put(FIELD_ANNOTATIONS, annotations);
        return annotationSet;
    }

    @NotNull
    private ValidatableResponse createAnnotation(String annotationId, String annotationSetId) {
        final JSONObject annotation = createAnnotationPayload(annotationId, annotationSetId);
        return request
                .body(annotation)
                .post(API_ANNOTATIONS)
                .then()
                .statusCode(201)
                .log().all();
    }

    @NotNull
    private JSONObject createAnnotationPayload(String annotationId, String annotationSetId) {
        final JSONObject createAnnotations = new JSONObject();
        createAnnotations.put(FIELD_ANNOTATION_SET_ID, annotationSetId);
        createAnnotations.put(FIELD_ID, annotationId);
        createAnnotations.put(FIELD_ANNOTATION_TYPE, VALUE_HIGHLIGHT);
        createAnnotations.put(FIELD_PAGE, 1);
        createAnnotations.put(FIELD_COLOR, VALUE_COLOR);

        final JSONArray comments = new JSONArray();
        final JSONObject comment = new JSONObject();
        comment.put(FIELD_CONTENT, VALUE_TEXT);
        comment.put(FIELD_ANNOTATION_ID, annotationId);
        comment.put(FIELD_ID, UUID.randomUUID().toString());
        comments.put(0, comment);
        createAnnotations.put(FIELD_COMMENTS, comments);

        final JSONArray rectangles = new JSONArray();
        final JSONObject rectangle = new JSONObject();
        rectangle.put(FIELD_ID, UUID.randomUUID().toString());
        rectangle.put(FIELD_ANNOTATION_ID, annotationId);
        rectangle.put("x", 0f);
        rectangle.put("y", 0f);
        rectangle.put("width", 10f);
        rectangle.put("height", 11f);
        rectangles.put(0, rectangle);
        createAnnotations.put(FIELD_RECTANGLES, rectangles);

        return createAnnotations;
    }

    @NotNull
    private JSONObject extractJsonObjectFromResponse(final ValidatableResponse response) {
        return response.extract().response().as(JSONObject.class);
    }
}
