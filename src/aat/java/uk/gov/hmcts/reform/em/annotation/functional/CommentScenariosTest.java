package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommentScenariosTest extends BaseTest {

    // === Common API paths ===
    private static final String API_COMMENTS = "/api/comments";
    private static final String API_ANNOTATIONS = "/api/annotations";
    private static final String API_ANNOTATION_SETS = "/api/annotation-sets";

    // === Common JSON field names ===
    private static final String FIELD_ID = "id";
    private static final String FIELD_CONTENT = "content";
    private static final String FIELD_ANNOTATION_ID = "annotationId";
    private static final String FIELD_DOCUMENT_ID = "documentId";
    private static final String FIELD_ANNOTATION_SET_ID = "annotationSetId";
    private static final String FIELD_ANNOTATION_TYPE = "annotationType";
    private static final String FIELD_PAGE = "page";
    private static final String FIELD_COLOR = "color";

    // === Common values ===
    private static final String DEFAULT_CONTENT = "text";
    private static final String UPDATED_CONTENT = "updated text";
    private static final String NEW_CONTENT = "new text";
    private static final String LOCATION_HEADER = "Location";
    private static final String HIGHLIGHT = "highlight";
    private static final String COLOR_CODE = "d1d1d1";

    public CommentScenariosTest(TestUtil testUtil) {
        super(testUtil);
    }

    @Test
    void shouldReturn201WhenCreateNewComment() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String commentId = UUID.randomUUID().toString();
        final ValidatableResponse response = createComment(annotationId, commentId);

        response
                .statusCode(201)
                .body(FIELD_ID, equalTo(commentId))
                .body(FIELD_CONTENT, equalTo(DEFAULT_CONTENT))
                .body(FIELD_ANNOTATION_ID, equalTo(annotationId))
                .header(LOCATION_HEADER, equalTo(API_COMMENTS + "/" + commentId))
                .log().all();
    }

    @Test
    void shouldReturn400WhenCreateNewCommentWithoutId() {
        final String annotationId = UUID.randomUUID().toString();
        final JSONObject comment = new JSONObject();
        comment.put(FIELD_CONTENT, DEFAULT_CONTENT);
        comment.put(FIELD_ANNOTATION_ID, annotationId);

        request
                .body(comment.toString())
                .post(API_COMMENTS)
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    void shouldReturn400WhenCreateNewCommentWithoutAnnotationId() {
        final String commentId = UUID.randomUUID().toString();
        final JSONObject comment = new JSONObject();
        comment.put(FIELD_CONTENT, DEFAULT_CONTENT);
        comment.put(FIELD_ID, commentId);

        request
                .body(comment.toString())
                .post(API_COMMENTS)
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserCreateNewComment() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String commentId = UUID.randomUUID().toString();
        final JSONObject comment = createCommentPayload(annotationId, commentId);

        unAuthenticatedRequest
                .body(comment.toString())
                .post(API_COMMENTS)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn500WhenCreateNewCommentWithNonExistentAnnotationId() {
        final String nonExistentAnnotationId = UUID.randomUUID().toString();
        final String commentId = UUID.randomUUID().toString();
        final JSONObject comment = createCommentPayload(nonExistentAnnotationId, commentId);

        request
                .body(comment.toString())
                .post(API_COMMENTS)
                .then()
                .statusCode(500)
                .log().all();
    }

    @Test
    void shouldReturn200WhenGetCommentById() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String commentId = UUID.randomUUID().toString();
        final ValidatableResponse response = createComment(annotationId, commentId);
        final String id = extractJsonObjectFromResponse(response).getString(FIELD_ID);

        request
                .get(API_COMMENTS + "/" + id)
                .then()
                .statusCode(200)
                .body(FIELD_ID, equalTo(commentId))
                .body(FIELD_CONTENT, equalTo(DEFAULT_CONTENT))
                .body(FIELD_ANNOTATION_ID, equalTo(annotationId))
                .log().all();
    }

    @Test
    void shouldReturn404WhenGetCommentNotFoundById() {
        final String commentId = UUID.randomUUID().toString();
        request
                .get(API_COMMENTS + "/" + commentId)
                .then()
                .statusCode(404)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserGetCommentById() {
        final String commentId = UUID.randomUUID().toString();
        unAuthenticatedRequest
                .get(API_COMMENTS + "/" + commentId)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenGetAllComments() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String commentId = UUID.randomUUID().toString();
        final ValidatableResponse response = createComment(annotationId, commentId);
        final String id = extractJsonObjectFromResponse(response).getString(FIELD_ID);
        assertNotNull(id);

        request
                .get(API_COMMENTS)
                .then()
                .statusCode(200)
                .body("size()", Matchers.greaterThanOrEqualTo(1))
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserGetAllComments() {
        unAuthenticatedRequest
                .get(API_COMMENTS)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenUpdateComment() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String commentId = UUID.randomUUID().toString();
        final ValidatableResponse response = createComment(annotationId, commentId);
        final JSONObject comment = extractJsonObjectFromResponse(response);
        comment.put(FIELD_CONTENT, UPDATED_CONTENT);

        request
                .body(comment.toString())
                .put(API_COMMENTS)
                .then()
                .statusCode(200)
                .body(FIELD_ID, equalTo(commentId))
                .body(FIELD_CONTENT, equalTo(UPDATED_CONTENT))
                .body(FIELD_ANNOTATION_ID, equalTo(annotationId))
                .log().all();
    }

    @Test
    void shouldReturn500WhenUpdateCommentWithNonExistentAnnotationId() {
        final String commentId = UUID.randomUUID().toString();
        final String nonExistentAnnotationId = UUID.randomUUID().toString();
        final JSONObject comment = createCommentPayload(nonExistentAnnotationId, commentId);

        request
                .body(comment.toString())
                .put(API_COMMENTS)
                .then()
                .statusCode(500)
                .log().all();
    }

    @Test
    void shouldReturn400WhenUpdateCommentWithoutId() {
        final JSONObject comment = new JSONObject();
        comment.put(FIELD_CONTENT, DEFAULT_CONTENT);
        comment.put(FIELD_ANNOTATION_ID, UUID.randomUUID());

        request
                .body(comment.toString())
                .put(API_COMMENTS)
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    void shouldReturn400WhenUpdateCommentWithoutAnnotationId() {
        final JSONObject comment = new JSONObject();
        comment.put(FIELD_CONTENT, DEFAULT_CONTENT);
        comment.put(FIELD_ID, UUID.randomUUID());

        request
                .body(comment.toString())
                .put(API_COMMENTS)
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserUpdateComment() {
        final JSONObject comment = new JSONObject();
        comment.put(FIELD_CONTENT, DEFAULT_CONTENT);
        comment.put(FIELD_ANNOTATION_ID, UUID.randomUUID());

        unAuthenticatedRequest
                .body(comment.toString())
                .put(API_COMMENTS)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenDeleteCommentById() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String commentId = UUID.randomUUID().toString();
        final ValidatableResponse response = createComment(annotationId, commentId);
        final String id = extractJsonObjectFromResponse(response).getString(FIELD_ID);
        final ValidatableResponse deletedResponse = deleteCommentById(id);

        deletedResponse.statusCode(200);
    }

    @Test
    void shouldReturn200WhenDeleteCommentByNonExistentId() {
        final String nonExistentId = UUID.randomUUID().toString();
        final ValidatableResponse deletedResponse = deleteCommentById(nonExistentId);

        deletedResponse.statusCode(200);
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserDeleteComment() {
        unAuthenticatedRequest
                .delete(API_COMMENTS + "/" + UUID.randomUUID())
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenTryToUpdateCommentAfterItHasBeenDeleted() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String commentId = UUID.randomUUID().toString();
        final ValidatableResponse response = createComment(annotationId, commentId);
        final JSONObject comment = extractJsonObjectFromResponse(response);
        final String id = comment.getString(FIELD_ID);
        deleteCommentById(id).statusCode(200);
        comment.put(FIELD_CONTENT, NEW_CONTENT);

        request
                .body(comment.toString())
                .put(API_COMMENTS)
                .then()
                .statusCode(200)
                .body(FIELD_ID, equalTo(id))
                .body(FIELD_CONTENT, equalTo(NEW_CONTENT))
                .body(FIELD_ANNOTATION_ID, equalTo(annotationId))
                .log().all();
    }

    private ValidatableResponse deleteCommentById(String commentId) {
        return request
                .delete(API_COMMENTS + "/" + commentId)
                .then()
                .log().all();
    }

    @NotNull
    private ValidatableResponse createComment(String annotationId, String commentId) {
        final JSONObject comment = createCommentPayload(annotationId, commentId);
        return request.log().all()
                .body(comment.toString())
                .post(API_COMMENTS)
                .then()
                .statusCode(201);
    }

    @NotNull
    private String createAnnotation(String newAnnotationSetId) {
        final UUID annotationId = UUID.randomUUID();
        final JSONObject createAnnotations = new JSONObject();
        createAnnotations.put(FIELD_ANNOTATION_SET_ID, newAnnotationSetId);
        createAnnotations.put(FIELD_ID, annotationId);
        createAnnotations.put(FIELD_ANNOTATION_TYPE, HIGHLIGHT);
        createAnnotations.put(FIELD_PAGE, 1);
        createAnnotations.put(FIELD_COLOR, COLOR_CODE);

        return request
                .body(createAnnotations)
                .post(API_ANNOTATIONS)
                .then()
                .statusCode(201)
                .body(FIELD_ID, equalTo(annotationId.toString()))
                .extract()
                .response()
                .getBody()
                .jsonPath()
                .get(FIELD_ID);
    }

    @NotNull
    private String createAnnotationSet() {
        final JSONObject jsonObject = new JSONObject();
        final UUID newAnnotationSetId = UUID.randomUUID();
        jsonObject.put(FIELD_DOCUMENT_ID, UUID.randomUUID().toString());
        jsonObject.put(FIELD_ID, newAnnotationSetId.toString());

        return request
                .body(jsonObject.toString())
                .post(API_ANNOTATION_SETS)
                .then()
                .statusCode(201)
                .body(FIELD_ID, equalTo(newAnnotationSetId.toString()))
                .extract()
                .response()
                .getBody()
                .jsonPath()
                .get(FIELD_ID);
    }

    @NotNull
    private JSONObject createCommentPayload(String annotationId, String commentId) {
        final JSONObject comment = new JSONObject();
        comment.put(FIELD_ID, commentId);
        comment.put(FIELD_CONTENT, DEFAULT_CONTENT);
        comment.put(FIELD_ANNOTATION_ID, annotationId);
        return comment;
    }

    @NotNull
    private JSONObject extractJsonObjectFromResponse(final ValidatableResponse response) {
        return response.extract().response().as(JSONObject.class);
    }
}
