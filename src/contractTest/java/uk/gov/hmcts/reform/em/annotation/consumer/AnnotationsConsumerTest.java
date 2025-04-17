package uk.gov.hmcts.reform.em.annotation.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.LambdaDslObject;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;
import java.util.UUID;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AnnotationsConsumerTest {

    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    public static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    public static final String SERVICE_AUTH_TOKEN = "Bearer someServiceAuthorizationToken";

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private final UUID exampleUserId = UUID.fromString("c38fd29e-fa2e-43d4-a599-2d3f2908565b");
    private final String exampleAnnotationId = "d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a";

    @Pact(provider = "annotation_api_annotation_provider", consumer = "annotation_api")
    public V4Pact createAnnotation201(PactDslWithProvider builder) {
        return builder
            .given("annotation is created successfully")
            .uponReceiving("A request to create an annotation")
            .path("/api/annotations")
            .method(HttpMethod.POST.toString())
            .headers(getHeaders())
            .body(createAnnotationDsl())
            .willRespondWith()
            .status(HttpStatus.CREATED.value())
            .body(createAnnotationDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createAnnotation201")
    void testCreateAnnotation201(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .body(createAnnotationDsl().getBody().toString())
            .post(mockServer.getUrl() + "/api/annotations")
            .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Pact(provider = "annotation_api_annotation_provider", consumer = "annotation_api")
    public V4Pact updateAnnotation200(PactDslWithProvider builder) {
        return builder
            .given("annotation is updated successfully")
            .uponReceiving("A request to update an annotation")
            .path("/api/annotations")
            .method(HttpMethod.PUT.toString())
            .headers(getHeaders())
            .body(createAnnotationDsl())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createAnnotationDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "updateAnnotation200")
    void testUpdateAnnotation200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .body(createAnnotationDsl().getBody().toString())
            .put(mockServer.getUrl() + "/api/annotations")
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = "annotation_api_annotation_provider", consumer = "annotation_api")
    public V4Pact getAnnotations200(PactDslWithProvider builder) {
        return builder
            .given("gets all annotations")
            .uponReceiving("A request to get all annotations")
            .path("/api/annotations")
            .method(HttpMethod.GET.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createAnnotationDslList())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getAnnotations200")
    void testGetAnnotations200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .get(mockServer.getUrl() + "/api/annotations")
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = "annotation_api_annotation_provider", consumer = "annotation_api")
    public V4Pact getAnnotation200(PactDslWithProvider builder) {
        return builder
            .given("gets the annotation by given id")
            .uponReceiving("A request to get a single annotation")
            .path("/api/annotations/" + exampleAnnotationId)
            .method(HttpMethod.GET.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createAnnotationDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getAnnotation200")
    void testGetAnnotation200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .get(mockServer.getUrl() + "/api/annotations/" + exampleAnnotationId)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    public Map<String, String> getHeaders() {
        return Map.of(
            SERVICE_AUTHORIZATION, SERVICE_AUTH_TOKEN,
            AUTHORIZATION, AUTH_TOKEN,
            "Content-Type", "application/json"
        );
    }

    private DslPart createAnnotationDslList() {
        return LambdaDsl.newJsonArrayMinLike(1, array ->
            array.object(this::getLambdaDslObject)).build();
    }


    private DslPart createAnnotationDsl() {
        return newJsonBody(this::getLambdaDslObject).build();
    }

    private void getLambdaDslObject(LambdaDslObject body) {
        body
            .stringType("color", "FFFF00")
            .eachLike("comments", comment -> comment
                .object("createdByDetails", details -> details
                    .stringType("forename", "Test")
                    .stringType("surname", "User")
                    .stringType("email", "test.user.annotations@example.com")
                )
                .datetime("createdDate", DATE_TIME_FORMAT)
                .uuid("createdBy", exampleUserId)
                .datetime("lastModifiedDate", DATE_TIME_FORMAT)
                .uuid("lastModifiedBy", exampleUserId)
                .uuid("id", UUID.fromString("dfc7e6a2-1a7c-4b81-a8a1-7da0a1f7c0f1"))
                .uuid("annotationId", UUID.fromString("e4f8e7b3-2b8d-4c92-b9b2-8eb1b2f8d1f2"))
                .object("lastModifiedByDetails", details -> details
                    .stringType("forename", "Test")
                    .stringType("surname", "User")
                    .stringType("email", "test.user.annotations@example.com")
                )
                .stringType("content", "This is a sample annotation comment text which can vary.")
            )
            .eachLike("rectangles", rectangle -> rectangle
                .object("createdByDetails", details -> details
                    .stringType("forename", "Test")
                    .stringType("surname", "User")
                    .stringType("email", "test.user.annotations@example.com")
                )
                .datetime("createdDate", DATE_TIME_FORMAT)
                .uuid("createdBy", exampleUserId)
                .datetime("lastModifiedDate", DATE_TIME_FORMAT)
                .uuid("lastModifiedBy", exampleUserId)
                .numberType("x", 100.5)
                .numberType("width", 250.0)
                .numberType("y", 55.2)
                .uuid("id", UUID.fromString("a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"))
                .uuid("annotationId", UUID.fromString("b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e"))
                .object("lastModifiedByDetails", details -> details
                    .stringType("forename", "Test")
                    .stringType("surname", "User")
                    .stringType("email", "test.user.annotations@example.com")
                )
                .numberType("height", 80.7)
            )
            .datetime("lastModifiedDate", DATE_TIME_FORMAT)
            .uuid("lastModifiedBy", exampleUserId)
            .stringType("jurisdiction", "AB")
            .stringType("commentHeader", "Comment Header")
            .stringType("caseId", "123456789012345")
            .stringMatcher("type", "AREA|HIGHLIGHT|POINT|TEXTBOX", "HIGHLIGHT")
            .object("lastModifiedByDetails", details -> details
                .stringType("forename", "Test")
                .stringType("surname", "User")
                .stringType("email", "test.user.annotations@example.com")
            )
            .eachLike("tags", tags -> tags
                .stringType("label", "Sample label")
                .stringType("name", "Sample name")
                .stringType("color", "FFFF00")
                .uuid("createdBy", exampleUserId)
            )
            .object("createdByDetails", details -> {
                details.stringType("forename", "Test");
                details.stringType("surname", "User");
                details.stringType("email", "test.user.annotations@example.com");
            })
            .uuid("annotationSetId", UUID.fromString("c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f"))
            .datetime("createdDate", DATE_TIME_FORMAT)
            .uuid("createdBy", exampleUserId)
            .uuid("id", UUID.fromString(exampleAnnotationId))
            .integerType("page", 1);
    }
}