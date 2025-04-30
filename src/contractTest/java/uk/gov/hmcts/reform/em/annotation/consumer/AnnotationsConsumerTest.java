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
class AnnotationsConsumerTest extends BaseConsumerTest {

    private static final String ANNOTATIONS_PROVIDER_NAME = "annotation_api_annotation_provider";
    private static final String ANNOTATIONS_API_BASE_PATH = "/api/annotations";

    private final String exampleAnnotationId = "d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a";

    @Pact(provider = ANNOTATIONS_PROVIDER_NAME, consumer = "annotation_api")
    public V4Pact createAnnotation201(PactDslWithProvider builder) {
        return builder
            .given("annotation is created successfully")
            .uponReceiving("A request to create an annotation")
            .path(ANNOTATIONS_API_BASE_PATH)
            .method(HttpMethod.POST.toString())
            .headers(getHeaders())
            .body(createAnnotationDsl())
            .willRespondWith()
            .status(HttpStatus.CREATED.value())
            .body(createAnnotationDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createAnnotation201", providerName = ANNOTATIONS_PROVIDER_NAME)
    void testCreateAnnotation201(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .body(createAnnotationDsl().getBody().toString())
            .post(mockServer.getUrl() + ANNOTATIONS_API_BASE_PATH)
            .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Pact(provider = ANNOTATIONS_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact updateAnnotation200(PactDslWithProvider builder) {
        return builder
            .given("annotation is updated successfully")
            .uponReceiving("A request to update an annotation")
            .path(ANNOTATIONS_API_BASE_PATH)
            .method(HttpMethod.PUT.toString())
            .headers(getHeaders())
            .body(createAnnotationDsl())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createAnnotationDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "updateAnnotation200", providerName = ANNOTATIONS_PROVIDER_NAME)
    void testUpdateAnnotation200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .body(createAnnotationDsl().getBody().toString())
            .put(mockServer.getUrl() + ANNOTATIONS_API_BASE_PATH)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = ANNOTATIONS_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact getAnnotations200(PactDslWithProvider builder) {
        return builder
            .given("gets all annotations")
            .uponReceiving("A request to get all annotations")
            .path(ANNOTATIONS_API_BASE_PATH)
            .method(HttpMethod.GET.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createAnnotationDslList())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getAnnotations200", providerName = ANNOTATIONS_PROVIDER_NAME)
    void testGetAnnotations200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .get(mockServer.getUrl() + ANNOTATIONS_API_BASE_PATH)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = ANNOTATIONS_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact getAnnotation200(PactDslWithProvider builder) {
        return builder
            .given("gets the annotation by given id")
            .uponReceiving("A request to get a single annotation")
            .path(ANNOTATIONS_API_BASE_PATH + "/" + exampleAnnotationId)
            .method(HttpMethod.GET.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createAnnotationDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getAnnotation200", providerName = ANNOTATIONS_PROVIDER_NAME)
    void testGetAnnotation200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .get(mockServer.getUrl() + ANNOTATIONS_API_BASE_PATH + "/" + exampleAnnotationId)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = ANNOTATIONS_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact deleteAnnotation200(PactDslWithProvider builder) {
        return builder
            .given("annotation exists for deletion")
            .uponReceiving("A request to delete an annotation")
            .path(ANNOTATIONS_API_BASE_PATH + "/" + exampleAnnotationId)
            .method(HttpMethod.DELETE.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "deleteAnnotation200", providerName = ANNOTATIONS_PROVIDER_NAME)
    void testDeleteAnnotation200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .delete(mockServer.getUrl() + ANNOTATIONS_API_BASE_PATH + "/" + exampleAnnotationId)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    private DslPart createAnnotationDslList() {
        return LambdaDsl.newJsonArrayMinLike(1, array ->
            array.object(this::getLambdaDslObject)).build();
    }

    private DslPart createAnnotationDsl() {
        return newJsonBody(this::getLambdaDslObject).build();
    }

    protected void getLambdaDslObject(LambdaDslObject body) {
        body
            .stringType("color", "FFFF00")
            .eachLike("comments", comment -> {
                buildAuditingFields(comment);
                comment
                    .uuid("id", UUID.randomUUID())
                    .uuid("annotationId", UUID.fromString(exampleAnnotationId))
                    .stringType("content", "This is a sample annotation comment text which can vary.");
            })
            .eachLike("rectangles", rectangle -> {
                buildAuditingFields(rectangle);

                rectangle
                    .numberType("x", 100.5)
                    .numberType("width", 250.0)
                    .numberType("y", 55.2)
                    .uuid("id", UUID.randomUUID())
                    .uuid("annotationId", UUID.fromString(exampleAnnotationId))
                    .numberType("height", 80.7);
            });

        buildAuditingFields(body);

        body
            .stringType("jurisdiction", "AB")
            .stringType("commentHeader", "Comment Header")
            .stringType("caseId", "123456789012345")
            .stringMatcher("type", "AREA|HIGHLIGHT|POINT|TEXTBOX", "HIGHLIGHT")
            .eachLike("tags", tags -> tags
                .stringType("label", "Sample label")
                .stringType("name", "Sample name")
                .stringType("color", "FFFF00")
                .uuid("createdBy", EXAMPLE_USER_ID)
            )
            .uuid("annotationSetId", UUID.fromString("c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f"))
            .uuid("id", UUID.fromString(exampleAnnotationId))
            .integerType("page", 1);
    }
}