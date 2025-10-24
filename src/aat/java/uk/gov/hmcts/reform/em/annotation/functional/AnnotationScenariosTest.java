package uk.gov.hmcts.reform.em.annotation.functional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

class AnnotationScenariosTest extends BaseTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // 🔹 API Endpoints
    private static final String API_ANNOTATIONS = "/api/annotations";
    private static final String API_ANNOTATION_SETS = "/api/annotation-sets";

    // 🔹 JSON field keys
    private static final String FIELD_ID = "id";
    private static final String FIELD_PAGE = "page";
    private static final String FIELD_COLOR = "color";
    private static final String FIELD_DOCUMENT_ID = "documentId";
    private static final String FIELD_ANNOTATION_ID = "annotationId";
    private static final String FIELD_ANNOTATION_SET_ID = "annotationSetId";
    private static final String FIELD_ANNOTATION_TYPE = "annotationType";
    private static final String FIELD_COMMENTS = "comments";
    private static final String FIELD_RECTANGLES = "rectangles";
    private static final String FIELD_CONTENT = "content";
    private static final String FIELD_X = "x";
    private static final String FIELD_Y = "y";
    private static final String FIELD_WIDTH = "width";
    private static final String FIELD_HEIGHT = "height";
    private static final String FIELD_CASE_ID = "caseId";
    private static final String FIELD_JURISDICTION = "jurisdiction";
    private static final String FIELD_COMMENT_HEADER = "commentHeader";

    // 🔹 Common values
    private static final String COLOR_DEFAULT = "d1d1d1";
    private static final String COLOR_UPDATED = "f1f1f1";
    private static final String COLOR_SECOND_UPDATE = "e1e1e1";
    private static final String ANNOTATION_TYPE_HIGHLIGHT = "highlight";
    private static final String COMMENT_TEXT = "text";
    private static final String LOCATION_HEADER = "Location";
    private static final String PUBLIC_LAW = "PUBLICLAW";

    // 🔹 Test data JSON
    public static final String CASE_DATA = """
        {
            "caseTitle": "title",
            "caseOwner": "owner",
            "caseCreationDate": null,
            "caseDescription": null,
            "caseComments": null
        }""";

    @Autowired
    public AnnotationScenariosTest(TestUtil testUtil) {
        super(testUtil);
    }

    @Test
    void shouldReturn201WhenCreateNewAnnotation() {
        final String annotationSetId = createAnnotationSet();
        final String annotationId = UUID.randomUUID().toString();
        final ValidatableResponse response = createAnnotation(annotationId, annotationSetId);

        response
                .statusCode(201)
                .body(FIELD_ID, equalTo(annotationId))
                .body(FIELD_PAGE, is(1))
                .body(FIELD_COLOR, is(COLOR_DEFAULT))
                .body(FIELD_COMMENTS, Matchers.hasSize(1))
                .body(FIELD_COMMENTS + "[0]." + FIELD_CONTENT, is(COMMENT_TEXT))
                .body(FIELD_COMMENTS + "[0]." + FIELD_ANNOTATION_ID, is(annotationId))
                .body(FIELD_RECTANGLES, Matchers.hasSize(1))
                .body(FIELD_RECTANGLES + "[0]." + FIELD_ANNOTATION_ID, is(annotationId))
                .body(FIELD_RECTANGLES + "[0]." + FIELD_X, is(0f))
                .body(FIELD_RECTANGLES + "[0]." + FIELD_Y, is(0f))
                .body(FIELD_RECTANGLES + "[0]." + FIELD_WIDTH, is(10f))
                .body(FIELD_RECTANGLES + "[0]." + FIELD_HEIGHT, is(11f))
                .header(LOCATION_HEADER, equalTo(API_ANNOTATIONS + "/" + annotationId))
                .log().all();
    }

    @Test
    void shouldReturn201WhenCreateNewAnnotationWithCaseId() throws JsonProcessingException {
        CaseDetails caseDetails = testUtil.createCase(
                PUBLIC_LAW,
                "CCD_BUNDLE_MVP_TYPE_ASYNC",
                objectMapper.readTree(CASE_DATA)
        );
        String caseId = String.valueOf(caseDetails.getId());

        final String annotationSetId = createAnnotationSet();
        final String annotationId = UUID.randomUUID().toString();

        JSONObject annotation = createAnnotationPayload(annotationId, annotationSetId);
        annotation.put(FIELD_JURISDICTION, PUBLIC_LAW);
        annotation.put(FIELD_CASE_ID, caseId);

        final ValidatableResponse response = request
                .body(annotation)
                .post(API_ANNOTATIONS)
                .then()
                .statusCode(201)
                .log().all();

        response
                .statusCode(201)
                .body(FIELD_ID, equalTo(annotationId))
                .body(FIELD_PAGE, is(1))
                .body(FIELD_COLOR, is(COLOR_DEFAULT))
                .body(FIELD_COMMENTS, Matchers.hasSize(1))
                .body(FIELD_COMMENTS + "[0]." + FIELD_CONTENT, is(COMMENT_TEXT))
                .body(FIELD_COMMENTS + "[0]." + FIELD_ANNOTATION_ID, is(annotationId))
                .body(FIELD_RECTANGLES, Matchers.hasSize(1))
                .body(FIELD_RECTANGLES + "[0]." + FIELD_ANNOTATION_ID, is(annotationId))
                .body(FIELD_RECTANGLES + "[0]." + FIELD_X, is(0f))
                .body(FIELD_RECTANGLES + "[0]." + FIELD_Y, is(0f))
                .body(FIELD_RECTANGLES + "[0]." + FIELD_WIDTH, is(10f))
                .body(FIELD_RECTANGLES + "[0]." + FIELD_HEIGHT, is(11f))
                .body(FIELD_CASE_ID, is(caseId))
                .body(FIELD_JURISDICTION, is(PUBLIC_LAW))
                .body(FIELD_COMMENT_HEADER, is("title owner"))
                .header(LOCATION_HEADER, equalTo(API_ANNOTATIONS + "/" + annotationId))
                .log().all();
    }

    @Test
    void shouldReturn400WhenCreateNewAnnotationWithBadPayload() {
        final String annotationSetId = createAnnotationSet();
        final JSONObject annotation = new JSONObject();
        annotation.put(FIELD_ANNOTATION_SET_ID, annotationSetId);
        annotation.put(FIELD_ANNOTATION_TYPE, ANNOTATION_TYPE_HIGHLIGHT);
        annotation.put(FIELD_PAGE, 1);
        annotation.put(FIELD_COLOR, COLOR_DEFAULT);

        request
                .body(annotation)
                .post(API_ANNOTATIONS)
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserCreateNewAnnotation() {
        final String annotationSetId = UUID.randomUUID().toString();
        final String annotationId = UUID.randomUUID().toString();

        final JSONObject annotation = new JSONObject();
        annotation.put(FIELD_ID, annotationId);
        annotation.put(FIELD_ANNOTATION_SET_ID, annotationSetId);
        annotation.put(FIELD_ANNOTATION_TYPE, ANNOTATION_TYPE_HIGHLIGHT);
        annotation.put(FIELD_PAGE, 1);
        annotation.put(FIELD_COLOR, COLOR_DEFAULT);

        unAuthenticatedRequest
                .body(annotation.toString())
                .post(API_ANNOTATIONS)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenGetAnnotationById() {
        final String annotationSetId = createAnnotationSet();
        final String annotationId = UUID.randomUUID().toString();
        final ValidatableResponse response = createAnnotation(annotationId, annotationSetId);
        final String id = extractJsonObjectFromResponse(response).getString(FIELD_ID);

        request
                .get(API_ANNOTATIONS + "/" + id)
                .then()
                .statusCode(200)
                .body(FIELD_ID, equalTo(annotationId))
                .body(FIELD_PAGE, is(1))
                .body(FIELD_COLOR, is(COLOR_DEFAULT))
                .body(FIELD_COMMENTS, Matchers.hasSize(1))
                .body(FIELD_COMMENTS + "[0]." + FIELD_CONTENT, is(COMMENT_TEXT))
                .body(FIELD_COMMENTS + "[0]." + FIELD_ANNOTATION_ID, is(annotationId))
                .body(FIELD_RECTANGLES, Matchers.hasSize(1))
                .body(FIELD_RECTANGLES + "[0]." + FIELD_ANNOTATION_ID, is(annotationId))
                .body(FIELD_RECTANGLES + "[0]." + FIELD_X, is(0f))
                .body(FIELD_RECTANGLES + "[0]." + FIELD_Y, is(0f))
                .body(FIELD_RECTANGLES + "[0]." + FIELD_WIDTH, is(10f))
                .body(FIELD_RECTANGLES + "[0]." + FIELD_HEIGHT, is(11f))
                .log().all();
    }

    @Test
    void shouldReturn404WhenGetAnnotationNotFoundById() {
        final String annotationId = UUID.randomUUID().toString();
        request
                .get(API_ANNOTATIONS + "/" + annotationId)
                .then()
                .statusCode(404)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserGetAnnotationById() {
        final String annotationId = UUID.randomUUID().toString();
        unAuthenticatedRequest
                .get(API_ANNOTATIONS + "/" + annotationId)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenGetAllAnnotations() {
        final String annotationSetId = createAnnotationSet();
        final String annotationId = UUID.randomUUID().toString();
        createAnnotation(annotationId, annotationSetId);

        request
                .get(API_ANNOTATIONS)
                .then()
                .statusCode(200)
                .body("size()", Matchers.greaterThanOrEqualTo(1))
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserGetAllAnnotations() {
        unAuthenticatedRequest
                .get(API_ANNOTATIONS)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenUpdateAnnotation() {
        final String annotationSetId = createAnnotationSet();
        final String annotationId = UUID.randomUUID().toString();
        final ValidatableResponse response = createAnnotation(annotationId, annotationSetId);
        final JSONObject annotation = extractJsonObjectFromResponse(response);
        annotation.put(FIELD_PAGE, 2);
        annotation.put(FIELD_COLOR, COLOR_UPDATED);

        request
                .body(annotation.toString())
                .put(API_ANNOTATIONS)
                .then()
                .statusCode(200)
                .body(FIELD_ID, equalTo(annotationId))
                .body(FIELD_PAGE, is(2))
                .body(FIELD_COLOR, is(COLOR_UPDATED))
                .body(FIELD_COMMENTS, Matchers.hasSize(1))
                .body("comments[0].content", is("text"))
                .body("comments[0].annotationId", is(annotationId))
                .body(FIELD_RECTANGLES, Matchers.hasSize(1))
                .body("rectangles[0].annotationId", is(annotationId))
                .body("rectangles[0].x", is(0f))
                .body("rectangles[0].y", is(0f))
                .body("rectangles[0].width", is(10f))
                .body("rectangles[0].height", is(11f))
                .log().all();
    }

    @Test
    void shouldReturn400WhenUpdateAnnotationWithoutId() {
        final String annotationSetId = createAnnotationSet();
        final String annotationId = UUID.randomUUID().toString();
        final ValidatableResponse response = createAnnotation(annotationId, annotationSetId);
        final JSONObject annotation = extractJsonObjectFromResponse(response);
        annotation.remove(FIELD_ID);

        request
                .body(annotation.toString())
                .put(API_ANNOTATIONS)
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserUpdateAnnotation() {
        final String annotationSetId = createAnnotationSet();
        final String annotationId = UUID.randomUUID().toString();
        final ValidatableResponse response = createAnnotation(annotationId, annotationSetId);
        final JSONObject annotation = extractJsonObjectFromResponse(response);

        unAuthenticatedRequest
                .body(annotation.toString())
                .put(API_ANNOTATIONS)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenDeleteAnnotationById() {
        final String annotationSetId = createAnnotationSet();
        final String annotationId = UUID.randomUUID().toString();
        final ValidatableResponse response = createAnnotation(annotationId, annotationSetId);
        final String id = extractJsonObjectFromResponse(response).getString(FIELD_ID);

        deleteAnnotationById(id).statusCode(200);
    }

    @Test
    void shouldReturn200WhenDeleteAnnotationByNonExistentId() {
        deleteAnnotationById(UUID.randomUUID().toString()).statusCode(200);
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserDeleteAnnotation() {
        unAuthenticatedRequest
                .delete(API_ANNOTATIONS + "/" + UUID.randomUUID())
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenUpdateAnnotationAfterItHasBeenDeleted() {
        final String annotationSetId = createAnnotationSet();
        final String annotationId = UUID.randomUUID().toString();
        final ValidatableResponse response = createAnnotation(annotationId, annotationSetId);
        final JSONObject annotation = extractJsonObjectFromResponse(response);
        final String id = annotation.getString(FIELD_ID);

        deleteAnnotationById(id).statusCode(200);
        annotation.put(FIELD_PAGE, 3);
        annotation.put(FIELD_COLOR, COLOR_SECOND_UPDATE);

        request
                .body(annotation.toString())
                .put(API_ANNOTATIONS)
                .then()
                .statusCode(200)
                .body(FIELD_ID, equalTo(annotationId))
                .body(FIELD_PAGE, is(3))
                .body(FIELD_COLOR, is(COLOR_SECOND_UPDATE))
                .body("comments", Matchers.hasSize(1))
                .body("comments[0].content", is("text"))
                .body("comments[0].annotationId", is(annotationId))
                .body("rectangles", Matchers.hasSize(1))
                .body("rectangles[0].annotationId", is(annotationId))
                .body("rectangles[0].x", is(0f))
                .body("rectangles[0].y", is(0f))
                .body("rectangles[0].width", is(10f))
                .body("rectangles[0].height", is(11f))
                .log().all();
    }

    @NotNull
    private ValidatableResponse deleteAnnotationById(String annotationId) {
        return request
                .delete(API_ANNOTATIONS + "/" + annotationId)
                .then()
                .log().all();
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
        createAnnotations.put(FIELD_ANNOTATION_TYPE, ANNOTATION_TYPE_HIGHLIGHT);
        createAnnotations.put(FIELD_PAGE, 1);
        createAnnotations.put(FIELD_COLOR, COLOR_DEFAULT);

        final JSONArray comments = new JSONArray();
        final JSONObject comment = new JSONObject();
        comment.put(FIELD_CONTENT, COMMENT_TEXT);
        comment.put(FIELD_ANNOTATION_ID, annotationId);
        comment.put(FIELD_ID, UUID.randomUUID().toString());
        comments.put(comment);
        createAnnotations.put(FIELD_COMMENTS, comments);

        final JSONArray rectangles = new JSONArray();
        final JSONObject rectangle = new JSONObject();
        rectangle.put(FIELD_ID, UUID.randomUUID().toString());
        rectangle.put(FIELD_ANNOTATION_ID, annotationId);
        rectangle.put(FIELD_X, 0f);
        rectangle.put(FIELD_Y, 0f);
        rectangle.put(FIELD_WIDTH, 10f);
        rectangle.put(FIELD_HEIGHT, 11f);
        rectangles.put(rectangle);
        createAnnotations.put(FIELD_RECTANGLES, rectangles);

        return createAnnotations;
    }

    @NotNull
    private JSONObject extractJsonObjectFromResponse(final ValidatableResponse response) {
        return response.extract().response().as(JSONObject.class);
    }
}
