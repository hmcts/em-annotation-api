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
@PactTestFor(providerName = "annotation_api_comment_provider")
class CommentConsumerTest extends BaseConsumerTest {

    private static final String COMMENT_PROVIDER_NAME = "annotation_api_comment_provider";
    private static final String COMMENTS_API_BASE_PATH = "/api/comments";

    private static final UUID EXAMPLE_COMMENT_ID = UUID.fromString("b3438f7d-0275-4063-9524-1a6d0b68636b");
    private static final UUID EXAMPLE_ANNOTATION_ID = UUID.fromString("a58e5f39-2b0f-48e2-b052-e932375b4f69");


    @Pact(provider = COMMENT_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact createComment201(PactDslWithProvider builder) {
        return builder
            .given("comment is created successfully")
            .uponReceiving("A request to create a comment")
            .path(COMMENTS_API_BASE_PATH)
            .method(HttpMethod.POST.toString())
            .headers(getHeaders())
            .body(createCommentDsl())
            .willRespondWith()
            .status(HttpStatus.CREATED.value())
            .body(createCommentDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createComment201")
    void testCreateComment201(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .body(createCommentDsl().getBody().toString())
            .post(mockServer.getUrl() + COMMENTS_API_BASE_PATH)
            .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Pact(provider = COMMENT_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact updateComment200(PactDslWithProvider builder) {
        return builder
            .given("comment is updated successfully")
            .uponReceiving("A request to update a comment")
            .path(COMMENTS_API_BASE_PATH)
            .method(HttpMethod.PUT.toString())
            .headers(getHeaders())
            .body(createCommentDsl())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createCommentDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "updateComment200")
    void testUpdateComment200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .body(createCommentDsl().getBody().toString())
            .put(mockServer.getUrl() + COMMENTS_API_BASE_PATH)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = COMMENT_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact getAllComments200(PactDslWithProvider builder) {
        return builder
            .given("comments exist")
            .uponReceiving("A request to get all comments")
            .path(COMMENTS_API_BASE_PATH)
            .method(HttpMethod.GET.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createCommentDslList())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getAllComments200")
    void testGetComments200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .get(mockServer.getUrl() + COMMENTS_API_BASE_PATH)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = COMMENT_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact getComment200(PactDslWithProvider builder) {
        return builder
            .given("a comment exists with the given id")
            .uponReceiving("A request to get a single comment")
            .path(COMMENTS_API_BASE_PATH + "/" + EXAMPLE_COMMENT_ID)
            .method(HttpMethod.GET.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createCommentDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getComment200")
    void testGetComment200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .get(mockServer.getUrl() + COMMENTS_API_BASE_PATH + "/" + EXAMPLE_COMMENT_ID)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = COMMENT_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact deleteComment200(PactDslWithProvider builder) {
        return builder
            .given("a comment exists for deletion")
            .uponReceiving("A request to delete a comment")
            .path(COMMENTS_API_BASE_PATH + "/" + EXAMPLE_COMMENT_ID)
            .method(HttpMethod.DELETE.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "deleteComment200")
    void testDeleteComment200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .delete(mockServer.getUrl() + COMMENTS_API_BASE_PATH + "/" + EXAMPLE_COMMENT_ID)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    private DslPart createCommentDsl() {
        return newJsonBody(this::buildCommentDslObject).build();
    }

    private DslPart createCommentDslList() {
        return LambdaDsl.newJsonArrayMinLike(1, array ->
            array.object(this::buildCommentDslObject)).build();
    }

    private void buildCommentDslObject(LambdaDslObject body) {
        buildAuditingFields(body);
        body
            .uuid("id", EXAMPLE_COMMENT_ID)
            .stringType("content", "This is a sample comment.")
            .uuid("annotationId", EXAMPLE_ANNOTATION_ID);
    }
}