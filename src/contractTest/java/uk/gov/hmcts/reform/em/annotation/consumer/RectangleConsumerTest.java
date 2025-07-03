package uk.gov.hmcts.reform.em.annotation.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.LambdaDslObject;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;

@Slf4j
class RectangleConsumerTest extends BaseConsumerTest {

    private static final String RECTANGLE_PROVIDER_NAME = "annotation_api_rectangle_provider";
    private static final String RECTANGLES_API_BASE_PATH = "/api/rectangles";

    private static final UUID EXAMPLE_RECTANGLE_ID = UUID.fromString("a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d");
    private static final UUID EXAMPLE_ANNOTATION_ID = UUID.fromString("d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a");

    @Pact(provider = RECTANGLE_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact createRectangle201(PactDslWithProvider builder) {
        return builder
            .given("rectangle is created successfully")
            .uponReceiving("A request to create a rectangle")
            .path(RECTANGLES_API_BASE_PATH)
            .method(HttpMethod.POST.toString())
            .headers(getHeaders())
            .body(createRectangleDsl())
            .willRespondWith()
            .status(HttpStatus.CREATED.value())
            .body(createRectangleDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createRectangle201", providerName = RECTANGLE_PROVIDER_NAME)
    void testCreateRectangle201(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .body(createRectangleDsl().getBody().toString())
            .post(mockServer.getUrl() + RECTANGLES_API_BASE_PATH)
            .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Pact(provider = RECTANGLE_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact updateRectangle200(PactDslWithProvider builder) {
        return builder
            .given("rectangle is updated successfully")
            .uponReceiving("A request to update a rectangle")
            .path(RECTANGLES_API_BASE_PATH)
            .method(HttpMethod.PUT.toString())
            .headers(getHeaders())
            .body(createRectangleDsl())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createRectangleDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "updateRectangle200", providerName = RECTANGLE_PROVIDER_NAME)
    void testUpdateRectangle200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .body(createRectangleDsl().getBody().toString())
            .put(mockServer.getUrl() + RECTANGLES_API_BASE_PATH)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = RECTANGLE_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact getAllRectangles200(PactDslWithProvider builder) {
        return builder
            .given("rectangles exist")
            .uponReceiving("A request to get all rectangles")
            .path(RECTANGLES_API_BASE_PATH)
            .method(HttpMethod.GET.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createRectangleDslList())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getAllRectangles200", providerName = RECTANGLE_PROVIDER_NAME)
    void testGetAllRectangles200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .get(mockServer.getUrl() + RECTANGLES_API_BASE_PATH)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = RECTANGLE_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact getRectangle200(PactDslWithProvider builder) {
        return builder
            .given("a rectangle exists with the given id")
            .uponReceiving("A request to get a single rectangle")
            .path(RECTANGLES_API_BASE_PATH + "/" + EXAMPLE_RECTANGLE_ID)
            .method(HttpMethod.GET.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createRectangleDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getRectangle200", providerName = RECTANGLE_PROVIDER_NAME)
    void testGetRectangle200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .get(mockServer.getUrl() + RECTANGLES_API_BASE_PATH + "/" + EXAMPLE_RECTANGLE_ID)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = RECTANGLE_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact deleteRectangle200(PactDslWithProvider builder) {
        return builder
            .given("a rectangle exists for deletion")
            .uponReceiving("A request to delete a rectangle")
            .path(RECTANGLES_API_BASE_PATH + "/" + EXAMPLE_RECTANGLE_ID)
            .method(HttpMethod.DELETE.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "deleteRectangle200", providerName = RECTANGLE_PROVIDER_NAME)
    void testDeleteRectangle200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .delete(mockServer.getUrl() + RECTANGLES_API_BASE_PATH + "/" + EXAMPLE_RECTANGLE_ID)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    private DslPart createRectangleDslList() {
        return LambdaDsl.newJsonArrayMinLike(1, array ->
            array.object(this::buildRectangleDslObject)).build();
    }

    private DslPart createRectangleDsl() {
        return newJsonBody(this::buildRectangleDslObject).build();
    }

    private void buildRectangleDslObject(LambdaDslObject body) {
        body
            .uuid("id", EXAMPLE_RECTANGLE_ID)
            .uuid("annotationId", EXAMPLE_ANNOTATION_ID)
            .numberType("x", 100.5)
            .numberType("y", 55.2)
            .numberType("width", 250.0)
            .numberType("height", 80.7);

        buildAuditingFields(body);
    }
}