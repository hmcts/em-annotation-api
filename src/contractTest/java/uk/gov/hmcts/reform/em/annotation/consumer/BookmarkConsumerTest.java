package uk.gov.hmcts.reform.em.annotation.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
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

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonArrayMinLike;
import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;

@Slf4j
class BookmarkConsumerTest extends BaseConsumerTest {

    private static final String BOOKMARK_PROVIDER_NAME = "annotation_api_bookmark_provider";
    private static final String BOOKMARKS_API_BASE_URI = "/api/bookmarks";
    private static final String BOOKMARKS_MULTIPLE_API_URI = "/api/bookmarks_multiple";
    private static final String DOCUMENT_BOOKMARKS_API_URI_FORMAT = "/api/%s/bookmarks";

    private static final UUID EXAMPLE_BOOKMARK_ID = UUID.fromString("1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d");
    private static final UUID ANOTHER_EXAMPLE_BOOKMARK_ID = UUID.fromString("5a6b7c8d-9e0f-1a2b-3c4d-5e6f7a8b9c0a");
    private static final UUID EXAMPLE_DOCUMENT_ID = UUID.fromString("2a3b4c5d-6e7f-8a9b-0c1d-2e3f4a5b6c7d");
    private static final UUID EXAMPLE_PARENT_BOOKMARK_ID = UUID.fromString("3a4b5c6d-7e8f-9a0b-1c2d-3e4f5a6b7c8d");
    private static final UUID EXAMPLE_PREVIOUS_BOOKMARK_ID = UUID.fromString("4a5b6c7d-8e9f-0a1b-2c3d-4e5f6a7b8c9d");

    @Pact(provider = BOOKMARK_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact createBookmark201(PactDslWithProvider builder) {
        return builder
            .given("bookmark is created successfully")
            .uponReceiving("A request to create a bookmark")
            .path(BOOKMARKS_API_BASE_URI)
            .method(HttpMethod.POST.toString())
            .headers(getHeaders())
            .body(createBookmarkDsl(EXAMPLE_BOOKMARK_ID))
            .willRespondWith()
            .status(HttpStatus.CREATED.value())
            .body(createBookmarkDsl(EXAMPLE_BOOKMARK_ID))
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createBookmark201", providerName = BOOKMARK_PROVIDER_NAME)
    void testCreateBookmark201(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .body(createBookmarkDsl(EXAMPLE_BOOKMARK_ID).getBody().toString())
            .post(mockServer.getUrl() + BOOKMARKS_API_BASE_URI)
            .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Pact(provider = BOOKMARK_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact updateBookmark200(PactDslWithProvider builder) {
        return builder
            .given("bookmark is updated successfully")
            .uponReceiving("A request to update a bookmark")
            .path(BOOKMARKS_API_BASE_URI)
            .method(HttpMethod.PUT.toString())
            .headers(getHeaders())
            .body(createBookmarkDsl(EXAMPLE_BOOKMARK_ID))
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createBookmarkDsl(EXAMPLE_BOOKMARK_ID))
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "updateBookmark200", providerName = BOOKMARK_PROVIDER_NAME)
    void testUpdateBookmark200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .body(createBookmarkDsl(EXAMPLE_BOOKMARK_ID).getBody().toString())
            .put(mockServer.getUrl() + BOOKMARKS_API_BASE_URI)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = BOOKMARK_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact updateMultipleBookmarks200(PactDslWithProvider builder) {
        return builder
            .given("bookmarks are updated successfully")
            .uponReceiving("A request to update multiple bookmarks")
            .path(BOOKMARKS_MULTIPLE_API_URI)
            .method(HttpMethod.PUT.toString())
            .headers(getHeaders())
            .body(createBookmarkListDsl())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createBookmarkListDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "updateMultipleBookmarks200", providerName = BOOKMARK_PROVIDER_NAME)
    void testUpdateMultipleBookmarks200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .body(createBookmarkListDsl().getBody().toString())
            .put(mockServer.getUrl() + BOOKMARKS_MULTIPLE_API_URI)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = BOOKMARK_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact getAllDocumentBookmarks200(PactDslWithProvider builder) {
        String documentBookmarksPath = String.format(DOCUMENT_BOOKMARKS_API_URI_FORMAT, EXAMPLE_DOCUMENT_ID);
        return builder
            .given("bookmarks exist for a document")
            .uponReceiving("A request to get all bookmarks for a document")
            .path(documentBookmarksPath)
            .method(HttpMethod.GET.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createBookmarkListDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getAllDocumentBookmarks200", providerName = BOOKMARK_PROVIDER_NAME)
    void testGetAllDocumentBookmarks200(MockServer mockServer) {
        String documentBookmarksPath = String.format(DOCUMENT_BOOKMARKS_API_URI_FORMAT, EXAMPLE_DOCUMENT_ID);
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .get(mockServer.getUrl() + documentBookmarksPath)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = BOOKMARK_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact deleteBookmark200(PactDslWithProvider builder) {
        String bookmarkUri = BOOKMARKS_API_BASE_URI + "/" + EXAMPLE_BOOKMARK_ID;
        return builder
            .given("bookmark exists for deletion")
            .uponReceiving("A request to delete a bookmark")
            .path(bookmarkUri)
            .method(HttpMethod.DELETE.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "deleteBookmark200", providerName = BOOKMARK_PROVIDER_NAME)
    void testDeleteBookmark200(MockServer mockServer) {
        String bookmarkUri = BOOKMARKS_API_BASE_URI + "/" + EXAMPLE_BOOKMARK_ID;
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .delete(mockServer.getUrl() + bookmarkUri)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Pact(provider = BOOKMARK_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact deleteMultipleBookmarks200(PactDslWithProvider builder) {
        return builder
            .given("bookmarks exist for multiple deletion")
            .uponReceiving("A request to delete multiple bookmarks")
            .path(BOOKMARKS_MULTIPLE_API_URI)
            .method(HttpMethod.DELETE.toString())
            .headers(getHeaders())
            .body(createDeleteBookmarkRequestDsl())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "deleteMultipleBookmarks200", providerName = BOOKMARK_PROVIDER_NAME)
    void testDeleteMultipleBookmarks200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .body(createDeleteBookmarkRequestDsl().getBody().toString())
            .delete(mockServer.getUrl() + BOOKMARKS_MULTIPLE_API_URI)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    private DslPart createBookmarkDsl(UUID bookmarkId) {
        return newJsonBody(body -> buildBookmarkDslObject(body, bookmarkId)).build();
    }

    private DslPart createBookmarkListDsl() {
        return newJsonArrayMinLike(1, array ->
            array.object(obj -> buildBookmarkDslObject(obj, EXAMPLE_BOOKMARK_ID))
                .object(obj -> buildBookmarkDslObject(obj, ANOTHER_EXAMPLE_BOOKMARK_ID, "Another Bookmark", 10))
        ).build();
    }

    private DslPart createDeleteBookmarkRequestDsl() {
        return newJsonBody(dsl -> {
            dsl.object("updated", updatedObj ->
                buildBookmarkDslObject(updatedObj, EXAMPLE_PARENT_BOOKMARK_ID, "Updated Parent Bookmark", 3));
            dsl.array("deleted", arr -> arr
                .uuid(EXAMPLE_BOOKMARK_ID.toString())
                .uuid(ANOTHER_EXAMPLE_BOOKMARK_ID.toString()
            ));
        }).build();
    }

    private void buildBookmarkDslObject(LambdaDslObject body, UUID bookmarkId) {
        buildBookmarkDslObject(body, bookmarkId, "My Bookmark", 5);
    }

    private void buildBookmarkDslObject(LambdaDslObject body, UUID bookmarkId, String name, int pageNumber) {
        body
            .uuid("id", bookmarkId)
            .stringType("name", name)
            .uuid("documentId", EXAMPLE_DOCUMENT_ID)
            .stringType("createdBy", EXAMPLE_USER_ID.toString())
            .integerType("pageNumber", pageNumber)
            .numberType("xCoordinate", 100.5)
            .numberType("yCoordinate", 200.75)
            .uuid("parent", EXAMPLE_PARENT_BOOKMARK_ID)
            .uuid("previous", EXAMPLE_PREVIOUS_BOOKMARK_ID);
    }
}