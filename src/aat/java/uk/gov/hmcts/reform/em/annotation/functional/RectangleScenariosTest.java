package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

class RectangleScenariosTest extends BaseTest {

    // === API Paths ===
    private static final String API_RECTANGLES = "/api/rectangles";
    private static final String API_RECTANGLES_ID = API_RECTANGLES + "/";
    private static final String API_ANNOTATIONS = "/api/annotations";
    private static final String API_ANNOTATION_SETS = "/api/annotation-sets";

    // === JSON Fields ===
    private static final String FIELD_ID = "id";
    private static final String FIELD_ANNOTATION_ID = "annotationId";
    private static final String FIELD_X = "x";
    private static final String FIELD_Y = "y";
    private static final String FIELD_WIDTH = "width";
    private static final String FIELD_HEIGHT = "height";

    // === Common Values ===
    private static final float DEFAULT_X = 1f;
    private static final float DEFAULT_Y = 2f;
    private static final float DEFAULT_WIDTH = 10f;
    private static final float DEFAULT_HEIGHT = 11f;
    private static final int DEFAULT_PAGE = 1;
    private static final String DEFAULT_ANNOTATION_TYPE = "highlight";
    private static final String DEFAULT_COLOR = "d1d1d1";

    // === Status Codes ===
    private static final int STATUS_OK = 200;
    private static final int STATUS_CREATED = 201;
    private static final int STATUS_BAD_REQUEST = 400;
    private static final int STATUS_UNAUTHORIZED = 401;
    private static final int STATUS_NOT_FOUND = 404;
    private static final int STATUS_INTERNAL_SERVER_ERROR = 500;

    // === JSON Matchers ===
    private static final String LOCATION_HEADER = "Location";
    private static final String HEADER_LOCATION_TEMPLATE = API_RECTANGLES_ID;

    public RectangleScenariosTest(TestUtil testUtil) {
        super(testUtil);
    }

    @Test
    void shouldReturn201WhenCreateNewRectangle() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String rectangleId = UUID.randomUUID().toString();

        final ValidatableResponse response = createRectangle(annotationId, rectangleId);

        response
                .statusCode(STATUS_CREATED)
                .body(FIELD_X, equalTo(DEFAULT_X))
                .body(FIELD_Y, equalTo(DEFAULT_Y))
                .body(FIELD_WIDTH, equalTo(DEFAULT_WIDTH))
                .body(FIELD_HEIGHT, equalTo(DEFAULT_HEIGHT))
                .body(FIELD_ANNOTATION_ID, equalTo(annotationId))
                .header(LOCATION_HEADER, equalTo(HEADER_LOCATION_TEMPLATE + rectangleId))
                .log().all();
    }

    @Test
    void shouldReturn400WhenCreateNewRectangleWithoutId() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final JSONObject rectanglePayload = createRectanglePayload(annotationId, UUID.randomUUID().toString());

        rectanglePayload.remove(FIELD_ID);

        request
                .body(rectanglePayload.toString())
                .post(API_RECTANGLES)
                .then()
                .statusCode(STATUS_BAD_REQUEST)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserCreateNewRectangle() {
        final String annotationId = UUID.randomUUID().toString();
        final JSONObject rectanglePayload = createRectanglePayload(annotationId, UUID.randomUUID().toString());

        unAuthenticatedRequest
                .body(rectanglePayload.toString())
                .post(API_RECTANGLES)
                .then()
                .statusCode(STATUS_UNAUTHORIZED)
                .log().all();
    }

    @Test
    void shouldReturn404WhenCreateNewRectangleWithNonExistentAnnotationId() {
        final JSONObject rectanglePayload =
                createRectanglePayload(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        request
                .body(rectanglePayload.toString())
                .post(API_RECTANGLES)
                .then()
                .statusCode(STATUS_NOT_FOUND)
                .log().all();
    }

    @Test
    void shouldReturn200WhenGetRectangleById() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final ValidatableResponse response = createRectangle(annotationId, UUID.randomUUID().toString());
        final String id = extractJsonObjectFromResponse(response).getString(FIELD_ID);

        request
                .get(API_RECTANGLES_ID + id)
                .then()
                .statusCode(STATUS_OK)
                .body(FIELD_X, equalTo(DEFAULT_X))
                .body(FIELD_Y, equalTo(DEFAULT_Y))
                .body(FIELD_WIDTH, equalTo(DEFAULT_WIDTH))
                .body(FIELD_HEIGHT, equalTo(DEFAULT_HEIGHT))
                .body(FIELD_ANNOTATION_ID, equalTo(annotationId))
                .log().all();
    }

    @Test
    void shouldReturn404WhenGetRectangleNotFoundById() {
        request
                .get(API_RECTANGLES_ID + UUID.randomUUID())
                .then()
                .statusCode(STATUS_NOT_FOUND)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserGetRectangleById() {
        unAuthenticatedRequest
                .get(API_RECTANGLES_ID + UUID.randomUUID())
                .then()
                .statusCode(STATUS_UNAUTHORIZED)
                .log().all();
    }

    @Test
    void shouldReturn200WhenGetAllRectangles() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        createRectangle(annotationId, UUID.randomUUID().toString());

        request
                .get(API_RECTANGLES)
                .then()
                .statusCode(STATUS_OK)
                .body("size()", Matchers.greaterThanOrEqualTo(1))
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserGetAllRectangles() {
        unAuthenticatedRequest
                .get(API_RECTANGLES)
                .then()
                .statusCode(STATUS_UNAUTHORIZED)
                .log().all();
    }

    @Test
    void shouldReturn200WhenUpdateRectangle() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String rectangleId = UUID.randomUUID().toString();
        final ValidatableResponse response = createRectangle(annotationId, rectangleId);
        final JSONObject rectangle = extractJsonObjectFromResponse(response);
        rectangle.put(FIELD_X, 3f);
        rectangle.put(FIELD_Y, 4f);

        request
                .body(rectangle.toString())
                .put(API_RECTANGLES)
                .then()
                .statusCode(STATUS_OK)
                .body(FIELD_ID, equalTo(rectangleId))
                .body(FIELD_X, equalTo(3f))
                .body(FIELD_Y, equalTo(4f))
                .body(FIELD_WIDTH, equalTo(DEFAULT_WIDTH))
                .body(FIELD_HEIGHT, equalTo(DEFAULT_HEIGHT))
                .body(FIELD_ANNOTATION_ID, equalTo(annotationId))
                .log().all();
    }

    @Test
    void shouldReturn500WhenUpdateRectangleWithNonExistentAnnotationId() {
        final JSONObject rectangle =
                createRectanglePayload(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        request
                .body(rectangle.toString())
                .put(API_RECTANGLES)
                .then()
                .statusCode(STATUS_INTERNAL_SERVER_ERROR)
                .log().all();
    }

    @Test
    void shouldReturn400WhenUpdateRectangleWithoutId() {
        final JSONObject rectangle =
                createRectanglePayload(createAnnotation(createAnnotationSet()), UUID.randomUUID().toString());
        rectangle.remove(FIELD_ID);

        request
                .body(rectangle.toString())
                .put(API_RECTANGLES)
                .then()
                .statusCode(STATUS_BAD_REQUEST)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserUpdateRectangle() {
        final JSONObject rectangle =
                createRectanglePayload(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        unAuthenticatedRequest
                .body(rectangle.toString())
                .put(API_RECTANGLES)
                .then()
                .statusCode(STATUS_UNAUTHORIZED)
                .log().all();
    }

    @Test
    void shouldReturn200WhenDeleteRectangleById() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final ValidatableResponse createdResponse = createRectangle(annotationId, UUID.randomUUID().toString());
        final String id = extractJsonObjectFromResponse(createdResponse).getString(FIELD_ID);

        deleteRectangleById(id).statusCode(STATUS_OK);
    }

    @Test
    void shouldReturn200WhenDeleteRectangleByNonExistentId() {
        deleteRectangleById(UUID.randomUUID().toString()).statusCode(STATUS_OK);
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserDeleteRectangle() {
        unAuthenticatedRequest
                .delete(API_RECTANGLES_ID + UUID.randomUUID())
                .then()
                .statusCode(STATUS_UNAUTHORIZED)
                .log().all();
    }

    @Test
    void shouldReturn200WhenUpdateRectangleAfterItHasBeenDeleted() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String rectangleId = UUID.randomUUID().toString();
        final ValidatableResponse response = createRectangle(annotationId, rectangleId);
        final JSONObject rectangle = extractJsonObjectFromResponse(response);
        final String id = rectangle.getString(FIELD_ID);

        deleteRectangleById(id).statusCode(STATUS_OK);
        rectangle.put(FIELD_X, 3f);
        rectangle.put(FIELD_Y, 4f);

        request
                .body(rectangle.toString())
                .put(API_RECTANGLES)
                .then()
                .statusCode(STATUS_OK)
                .body(FIELD_ID, equalTo(rectangleId))
                .body(FIELD_X, equalTo(3f))
                .body(FIELD_Y, equalTo(4f))
                .body(FIELD_WIDTH, equalTo(DEFAULT_WIDTH))
                .body(FIELD_HEIGHT, equalTo(DEFAULT_HEIGHT))
                .body(FIELD_ANNOTATION_ID, equalTo(annotationId))
                .log().all();
    }

    @NotNull
    private ValidatableResponse deleteRectangleById(String rectangleId) {
        return request
                .delete(API_RECTANGLES_ID + rectangleId)
                .then()
                .log().all();
    }

    @NotNull
    private ValidatableResponse createRectangle(String annotationId, String rectangleId) {
        final JSONObject rectangle = createRectanglePayload(annotationId, rectangleId);
        return request.log().all()
                .body(rectangle.toString())
                .post(API_RECTANGLES)
                .then()
                .statusCode(STATUS_CREATED);
    }

    @NotNull
    private String createAnnotation(final String newAnnotationSetId) {
        final UUID annotationId = UUID.randomUUID();
        final JSONObject annotation = new JSONObject();
        annotation.put("annotationSetId", newAnnotationSetId);
        annotation.put(FIELD_ID, annotationId);
        annotation.put("annotationType", DEFAULT_ANNOTATION_TYPE);
        annotation.put("page", DEFAULT_PAGE);
        annotation.put("color", DEFAULT_COLOR);

        return request
                .body(annotation)
                .post(API_ANNOTATIONS)
                .then()
                .statusCode(STATUS_CREATED)
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
        jsonObject.put("documentId", UUID.randomUUID().toString());
        jsonObject.put(FIELD_ID, newAnnotationSetId.toString());

        return request
                .body(jsonObject.toString())
                .post(API_ANNOTATION_SETS)
                .then()
                .statusCode(STATUS_CREATED)
                .body(FIELD_ID, equalTo(newAnnotationSetId.toString()))
                .extract()
                .response()
                .getBody()
                .jsonPath()
                .get(FIELD_ID);
    }

    @NotNull
    private JSONObject createRectanglePayload(String annotationId, String rectangleId) {
        final JSONObject rectangle = new JSONObject();
        rectangle.put(FIELD_ID, rectangleId);
        rectangle.put(FIELD_ANNOTATION_ID, annotationId);
        rectangle.put(FIELD_X, DEFAULT_X);
        rectangle.put(FIELD_Y, DEFAULT_Y);
        rectangle.put(FIELD_WIDTH, DEFAULT_WIDTH);
        rectangle.put(FIELD_HEIGHT, DEFAULT_HEIGHT);
        return rectangle;
    }

    @NotNull
    private JSONObject extractJsonObjectFromResponse(final ValidatableResponse response) {
        return response.extract().response().as(JSONObject.class);
    }
}
