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
@PactTestFor(providerName = "annotation_api_annotation_set_provider")
class AnnotationSetConsumerTest extends BaseConsumerTest {

    private static final String ANNOTATION_SET_PROVIDER_NAME = "annotation_api_annotation_set_provider";
    private static final String ANNOTATION_SET_API_BASE_PATH = "/api/annotation-sets";
    private static final UUID EXAMPLE_ANNOTATION_SET_ID = UUID.fromString("4f6fe7a2-b8a6-4f0a-9f7c-8d9e1b0c9b3a");

    private final AnnotationsConsumerTest annotationsConsumerTestHelper = new AnnotationsConsumerTest();

    @Pact(provider = ANNOTATION_SET_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact createAnnotationSet201(PactDslWithProvider builder) {
        return builder
            .given("annotation set is created successfully")
            .uponReceiving("A request to create an annotation set")
            .path(ANNOTATION_SET_API_BASE_PATH)
            .method(HttpMethod.POST.toString())
            .headers(getHeaders())
            .body(createAnnotationSetDsl())
            .willRespondWith()
            .status(HttpStatus.CREATED.value())
            .body(createAnnotationSetDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createAnnotationSet201")
    void testCreateAnnotationSet201(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .body(createAnnotationSetDsl().getBody().toString())
            .post(mockServer.getUrl() + ANNOTATION_SET_API_BASE_PATH)
            .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Pact(provider = ANNOTATION_SET_PROVIDER_NAME, consumer = "annotation_api")
    public V4Pact updateAnnotationSet200(PactDslWithProvider builder) {
        return builder
            .given("annotation set is updated successfully")
            .uponReceiving("A request to update an annotation set")
            .path(ANNOTATION_SET_API_BASE_PATH)
            .method(HttpMethod.PUT.toString())
            .headers(getHeaders())
            .body(createAnnotationSetDsl())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createAnnotationSetDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "updateAnnotationSet200")
    void testUpdateAnnotationSet200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .body(createAnnotationSetDsl().getBody().toString())
            .put(mockServer.getUrl() + ANNOTATION_SET_API_BASE_PATH)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = ANNOTATION_SET_PROVIDER_NAME, consumer = "annotation_api")
    public V4Pact getAnnotationSets200(PactDslWithProvider builder) {
        return builder
            .given("annotation sets exist")
            .uponReceiving("A request to get all annotation sets")
            .path(ANNOTATION_SET_API_BASE_PATH)
            .method(HttpMethod.GET.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createAnnotationSetDslList())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getAnnotationSets200")
    void testGetAnnotationSets200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .get(mockServer.getUrl() + ANNOTATION_SET_API_BASE_PATH)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = ANNOTATION_SET_PROVIDER_NAME, consumer = "annotation_api")
    public V4Pact getAnnotationSet200(PactDslWithProvider builder) {
        return builder
            .given("an annotation set exists with the given id")
            .uponReceiving("A request to get a single annotation set")
            .path(ANNOTATION_SET_API_BASE_PATH + "/" + EXAMPLE_ANNOTATION_SET_ID)
            .method(HttpMethod.GET.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createAnnotationSetDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getAnnotationSet200")
    void testGetAnnotationSet200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .get(mockServer.getUrl() + ANNOTATION_SET_API_BASE_PATH + "/" + EXAMPLE_ANNOTATION_SET_ID)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = ANNOTATION_SET_PROVIDER_NAME, consumer = "annotation_api")
    public V4Pact deleteAnnotationSet200(PactDslWithProvider builder) {
        return builder
            .given("an annotation set exists for deletion")
            .uponReceiving("A request to delete an annotation set")
            .path(ANNOTATION_SET_API_BASE_PATH + "/" + EXAMPLE_ANNOTATION_SET_ID)
            .method(HttpMethod.DELETE.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "deleteAnnotationSet200")
    void testDeleteAnnotationSet200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .delete(mockServer.getUrl() + ANNOTATION_SET_API_BASE_PATH + "/" + EXAMPLE_ANNOTATION_SET_ID)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    private DslPart createAnnotationSetDslList() {
        return LambdaDsl.newJsonArrayMinLike(1, array ->
            array.object(this::buildAnnotationSetBody)
        ).build();
    }


    private DslPart createAnnotationSetDsl() {
        return newJsonBody(this::buildAnnotationSetBody).build();
    }

    private void buildAnnotationSetBody(LambdaDslObject body) {
        body
            .uuid("id", EXAMPLE_ANNOTATION_SET_ID)
            .stringType("documentId", "f401727b-5a50-40bb-ac4d-87dc34910b6e");
        buildAuditingFields(body);
        body.eachLike("annotations", annotationsConsumerTestHelper::getLambdaDslObject);
    }
}