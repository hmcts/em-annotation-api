package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;
import uk.gov.hmcts.reform.em.test.retry.RetryExtension;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = {TestUtil.class})
@TestPropertySource(value = "classpath:application.yml")
@ExtendWith({SerenityJUnit5Extension.class, SpringExtension.class})
@WithTags({@WithTag("testType:Functional")})
class RectangleScenariosTest {

    @Autowired
    private TestUtil testUtil;

    @Value("${test.url}")
    private String testUrl;

    @RegisterExtension
    RetryExtension retryExtension = new RetryExtension(3);

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
    void shouldReturn201WhenCreateNewRectangle() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String rectangleId = UUID.randomUUID().toString();

        final ValidatableResponse response = createRectangle(annotationId, rectangleId);

        response
                .statusCode(201)
                .body("x", equalTo(1f))
                .body("y", equalTo(2f))
                .body("width", equalTo(10f))
                .body("height", equalTo(11f))
                .body("annotationId", equalTo(annotationId))
                .header("Location", equalTo("/api/rectangles/" + rectangleId))
                .log().all();
    }

    @Test
    void shouldReturn400WhenCreateNewRectangleWithoutId() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String rectangleId = UUID.randomUUID().toString();
        final JSONObject rectanglePayload = createRectanglePayload(annotationId, rectangleId);

        rectanglePayload.remove("id");

        request
                .body(rectanglePayload.toString())
                .post("/api/rectangles")
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserCreateNewRectangle() {
        final String annotationId = UUID.randomUUID().toString();
        final String rectangleId = UUID.randomUUID().toString();
        final JSONObject rectanglePayload = createRectanglePayload(annotationId, rectangleId);

        unAuthenticatedRequest
                .body(rectanglePayload.toString())
                .post("/api/rectangles")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn404WhenCreateNewRectangleWithNonExistentAnnotationId() {

        final String nonExistentAnnotationId = UUID.randomUUID().toString();
        final String rectangleId = UUID.randomUUID().toString();
        final JSONObject rectanglePayload = createRectanglePayload(nonExistentAnnotationId, rectangleId);
        request
            .body(rectanglePayload.toString())
            .post("/api/rectangles")
            .then()
            .statusCode(404)
            .log().all();
    }

    @Test
    void shouldReturn200WhenGetRectangleById() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String rectangleId = UUID.randomUUID().toString();
        final ValidatableResponse response = createRectangle(annotationId, rectangleId);
        final String id = extractJsonObjectFromResponse(response).getString("id");

        request
                .get("/api/rectangles/" + id)
                .then()
                .statusCode(200)
                .body("x", equalTo(1f))
                .body("y", equalTo(2f))
                .body("width", equalTo(10f))
                .body("height", equalTo(11f))
                .body("annotationId", equalTo(annotationId))
                .log().all();
    }

    @Test
    void shouldReturn404WhenGetRectangleNotFoundById() {
        final String rectangleId = UUID.randomUUID().toString();
        request
                .get("/api/rectangles/" + rectangleId)
                .then()
                .statusCode(404)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserGetRectangleById() {
        final String rectangleId = UUID.randomUUID().toString();
        unAuthenticatedRequest
                .get("/api/rectangles/" + rectangleId)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenGetAllRectangles() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String rectangleId = UUID.randomUUID().toString();
        createRectangle(annotationId, rectangleId);

        request
                .get("/api/rectangles")
                .then()
                .statusCode(200)
                .body("size()", Matchers.greaterThanOrEqualTo(1))
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserGetAllRectangles() {
        unAuthenticatedRequest
                .get("/api/rectangles")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenUpdateRectangle() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String rectangleId = UUID.randomUUID().toString();
        final ValidatableResponse response = createRectangle(annotationId, rectangleId);
        final JSONObject rectangle = extractJsonObjectFromResponse(response);
        rectangle.put("x", 3f);
        rectangle.put("y", 4f);

        request
                .body(rectangle.toString())
                .put("/api/rectangles")
                .then()
                .statusCode(200)
                .body("id", equalTo(rectangleId))
                .body("x", equalTo(3f))
                .body("y", equalTo(4f))
                .body("width", equalTo(10f))
                .body("height", equalTo(11f))
                .body("annotationId", equalTo(annotationId))
                .log().all();
    }

    @Test
    void shouldReturn500WhenUpdateRectangleWithNonExistentAnnotationId() {
        final String rectangleId = UUID.randomUUID().toString();
        final String nonExistentAnnotationId = UUID.randomUUID().toString();
        final JSONObject rectangle = createRectanglePayload(nonExistentAnnotationId, rectangleId);
        request
                .body(rectangle.toString())
                .put("/api/rectangles")
                .then()
                .statusCode(500)
                .log().all();
    }

    @Test
    void shouldReturn400WhenUpdateRectangleWithoutId() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String rectangleId = UUID.randomUUID().toString();
        final JSONObject rectangle = createRectanglePayload(annotationId, rectangleId);

        rectangle.remove("id");

        request
                .body(rectangle.toString())
                .put("/api/rectangles")
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserUpdateRectangle() {
        final String annotationId = UUID.randomUUID().toString();
        final String rectangleId = UUID.randomUUID().toString();
        final JSONObject rectangle = createRectanglePayload(annotationId, rectangleId);

        unAuthenticatedRequest
                .body(rectangle.toString())
                .put("/api/rectangles")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenDeleteRectangleById() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String rectangleId = UUID.randomUUID().toString();
        final ValidatableResponse createdResponse = createRectangle(annotationId, rectangleId);
        final String id = extractJsonObjectFromResponse(createdResponse).getString("id");

        final ValidatableResponse deletedResponse = deleteRectangleById(id);

        deletedResponse.statusCode(200);
    }

    @Test
    void shouldReturn200WhenDeleteRectangleByNonExistentId() {
        final String nonExistentRectangleId = UUID.randomUUID().toString();
        final ValidatableResponse deletedResponse = deleteRectangleById(nonExistentRectangleId);

        deletedResponse.statusCode(200);
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserDeleteRectangle() {
        unAuthenticatedRequest
                .delete("/api/rectangles/" + UUID.randomUUID())
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenUpdateRectangleAfterItHasBeenDeleted() {
        final String newAnnotationSetId = createAnnotationSet();
        final String annotationId = createAnnotation(newAnnotationSetId);
        final String rectangleId = UUID.randomUUID().toString();
        final ValidatableResponse response = createRectangle(annotationId, rectangleId);
        final JSONObject rectangle = extractJsonObjectFromResponse(response);
        final String id = rectangle.getString("id");
        deleteRectangleById(id).statusCode(200);
        rectangle.put("x", 3f);
        rectangle.put("y", 4f);

        request
                .body(rectangle.toString())
                .put("/api/rectangles")
                .then()
                .statusCode(200)
                .body("id", equalTo(rectangleId))
                .body("x", equalTo(3f))
                .body("y", equalTo(4f))
                .body("width", equalTo(10f))
                .body("height", equalTo(11f))
                .body("annotationId", equalTo(annotationId))
                .log().all();
    }

    @NotNull
    private ValidatableResponse deleteRectangleById(String rectangleId) {
        return request
                .delete("/api/rectangles/" + rectangleId)
                .then()
                .log().all();
    }

    @NotNull
    private ValidatableResponse createRectangle(String annotationId, String rectangleId) {
        final JSONObject rectangle = createRectanglePayload(annotationId, rectangleId);
        return request.log().all()
                .body(rectangle.toString())
                .post("/api/rectangles")
                .then()
                .statusCode(201);
    }

    @NotNull
    private String createAnnotation(final String newAnnotationSetId) {
        final UUID annotationId = UUID.randomUUID();
        final JSONObject createAnnotations = new JSONObject();
        createAnnotations.put("annotationSetId", newAnnotationSetId);
        createAnnotations.put("id", annotationId);
        createAnnotations.put("annotationType", "highlight");
        createAnnotations.put("page", 1);
        createAnnotations.put("color", "d1d1d1");

        return request
                .body(createAnnotations)
                .post("/api/annotations")
                .then()
                .statusCode(201)
                .body("id", equalTo(annotationId.toString()))
                .extract()
                .response()
                .getBody()
                .jsonPath()
                .get("id");
    }

    @NotNull
    private String createAnnotationSet() {
        final JSONObject jsonObject = new JSONObject();
        final UUID newAnnotationSetId = UUID.randomUUID();
        jsonObject.put("documentId", UUID.randomUUID().toString());
        jsonObject.put("id", newAnnotationSetId.toString());

        return request
                .body(jsonObject.toString())
                .post("/api/annotation-sets")
                .then()
                .statusCode(201)
                .body("id", equalTo(newAnnotationSetId.toString()))
                .extract()
                .response()
                .getBody()
                .jsonPath()
                .get("id");
    }

    @NotNull
    private JSONObject createRectanglePayload(String annotationId, String rectangleId) {
        final JSONObject rectangle = new JSONObject();
        rectangle.put("id", rectangleId);
        rectangle.put("annotationId", annotationId);
        rectangle.put("x", 1f);
        rectangle.put("y", 2f);
        rectangle.put("width", 10f);
        rectangle.put("height", 11f);
        return rectangle;
    }

    @NotNull
    private JSONObject extractJsonObjectFromResponse(final ValidatableResponse response) {
        return response.extract().response().as(JSONObject.class);
    }
}
