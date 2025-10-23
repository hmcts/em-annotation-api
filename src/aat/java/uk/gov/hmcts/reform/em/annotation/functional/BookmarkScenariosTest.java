package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.response.ValidatableResponse;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;

class BookmarkScenariosTest extends BaseTest {

    private UUID documentId;

    public static final String BOOKMARKS = "/%s/bookmarks";
    private static final String API_BASE = "/api";
    private static final String API_BOOKMARKS = API_BASE + "/bookmarks";
    private static final String API_BOOKMARKS_MULTIPLE = API_BASE + "/bookmarks_multiple";
    private static final String FIELD_ID = "id";
    private static final String FIELD_DOCUMENT_ID = "documentId";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_CREATED_BY = "createdBy";
    private static final String FIELD_PAGE_NUMBER = "pageNumber";
    private static final String FIELD_X_COORD = "xCoordinate";
    private static final String FIELD_Y_COORD = "yCoordinate";
    private static final String FIELD_PARENT = "parent";
    private static final String FIELD_PREVIOUS = "previous";
    private static final String FIELD_DELETED = "deleted";
    private static final String CREATED_BY_USER = "user";
    private static final String BOOKMARK_NAME = "Bookmark for test";
    private static final float DEFAULT_COORD = 100f;
    private static final int DEFAULT_PAGE = 1;

    @Autowired
    public BookmarkScenariosTest(TestUtil testUtil) {
        super(testUtil);
    }

    @BeforeEach
    public void setup() {
        documentId = UUID.randomUUID();
    }

    @Test
    void shouldReturn201WhenCreateNewBookmark() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);

        response
                .statusCode(201)
                .body(FIELD_ID, equalTo(bookmarkId.toString()))
                .body(FIELD_DOCUMENT_ID, equalTo(documentId.toString()))
                .body(FIELD_NAME, equalTo(BOOKMARK_NAME))
                .body(FIELD_CREATED_BY, equalTo(CREATED_BY_USER))
                .body(FIELD_PAGE_NUMBER, equalTo(DEFAULT_PAGE))
                .body(FIELD_X_COORD, equalTo(DEFAULT_COORD))
                .body(FIELD_Y_COORD, equalTo(DEFAULT_COORD))
                .body(FIELD_PARENT, notNullValue())
                .body(FIELD_PREVIOUS, notNullValue())
                .header("Location", equalTo(API_BOOKMARKS + "/" + bookmarkId))
                .log().all();
    }

    @Test
    void shouldReturn400WhenCreateNewBookmarkWithoutId() {
        final UUID bookmarkId = UUID.randomUUID();
        final JSONObject bookmarkRequestPayload = createBookmarkRequestPayload(bookmarkId);
        bookmarkRequestPayload.remove(FIELD_ID);

        request.body(bookmarkRequestPayload.toString())
                .post(API_BOOKMARKS)
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserCreateNewBookmark() {
        final UUID bookmarkId = UUID.randomUUID();
        final JSONObject bookmarkRequestPayload = createBookmarkRequestPayload(bookmarkId);

        unAuthenticatedRequest.body(bookmarkRequestPayload.toString())
                .post(API_BOOKMARKS)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn409WhenCreateNewBookmarkWithoutMandatoryField() {
        final UUID bookmarkId = UUID.randomUUID();
        final JSONObject bookmarkRequestPayload = createBookmarkRequestPayload(bookmarkId);
        bookmarkRequestPayload.remove(FIELD_NAME);

        request.body(bookmarkRequestPayload.toString())
                .post(API_BOOKMARKS)
                .then()
                .statusCode(409)
                .log().all();
    }

    @Test
    void shouldReturn200WhenGetAllBookmarksByDocumentId() {
        final UUID bookmarkId = UUID.randomUUID();
        final JSONObject jsonObject = createBookmarkRequestPayload(bookmarkId);
        jsonObject.remove(FIELD_CREATED_BY);

        final ValidatableResponse response = request.log().all()
                .body(jsonObject.toString())
                .post(API_BOOKMARKS)
                .then()
                .statusCode(201);

        final JSONObject newJsonObject = extractJsonObjectFromResponse(response);
        final String id = newJsonObject.getString(FIELD_DOCUMENT_ID);

        request.get(String.format(API_BASE + BOOKMARKS, id))
                .then()
                .statusCode(200)
                .body(FIELD_ID, equalTo(List.of(bookmarkId.toString())))
                .body(FIELD_DOCUMENT_ID, equalTo(List.of(id)))
                .body(FIELD_NAME, equalTo(List.of(BOOKMARK_NAME)))
                .body(FIELD_PAGE_NUMBER, equalTo(List.of(DEFAULT_PAGE)))
                .body(FIELD_X_COORD, equalTo(List.of(DEFAULT_COORD)))
                .body(FIELD_Y_COORD, equalTo(List.of(DEFAULT_COORD)))
                .log().all();
    }

    @Test
    void shouldReturn200WhenGetAllBookmarksMoreThan20ByDocumentId() {
        List<String> bookMarks = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            final UUID bookmarkId = UUID.randomUUID();
            final JSONObject jsonObject = createBookmarkRequestPayload(bookmarkId);
            jsonObject.remove(FIELD_CREATED_BY);

            request.body(jsonObject.toString())
                    .post(API_BOOKMARKS)
                    .then()
                    .statusCode(201);
            bookMarks.add(bookmarkId.toString());
        }

        request.get(String.format(API_BASE + BOOKMARKS, documentId))
                .then()
                .statusCode(200)
                .body(FIELD_ID, containsInAnyOrder(bookMarks.toArray()))
                .log().all();
    }

    @Test
    void shouldReturn204WhenResponseBodyIsEmptyForGivenDocId() {
        request.get(String.format(API_BASE + BOOKMARKS, UUID.randomUUID()))
                .then()
                .statusCode(204)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserGetBookmarksById() {
        unAuthenticatedRequest.get(String.format(API_BASE + BOOKMARKS, UUID.randomUUID()))
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenUpdateBookmark() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);
        final JSONObject jsonObject = extractJsonObjectFromResponse(response);
        jsonObject.put(FIELD_NAME, "new name");

        request.body(jsonObject.toString())
                .put(API_BOOKMARKS)
                .then()
                .statusCode(200)
                .body(FIELD_ID, equalTo(bookmarkId.toString()))
                .body(FIELD_DOCUMENT_ID, equalTo(documentId.toString()))
                .body(FIELD_NAME, equalTo("new name"))
                .body(FIELD_CREATED_BY, equalTo(CREATED_BY_USER))
                .body(FIELD_PAGE_NUMBER, equalTo(DEFAULT_PAGE))
                .body(FIELD_X_COORD, equalTo(DEFAULT_COORD))
                .body(FIELD_Y_COORD, equalTo(DEFAULT_COORD))
                .body(FIELD_PARENT, notNullValue())
                .body(FIELD_PREVIOUS, notNullValue())
                .log().all();
    }

    @Test
    void shouldReturn400WhenUpdateBookmarkWithBadRequestPayload() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);
        final JSONObject jsonObject = extractJsonObjectFromResponse(response);
        jsonObject.remove(FIELD_ID);

        request.body(jsonObject.toString())
                .put(API_BOOKMARKS)
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserUpdateBookmark() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);
        final JSONObject jsonObject = extractJsonObjectFromResponse(response);

        unAuthenticatedRequest.body(jsonObject.toString())
                .put(API_BOOKMARKS)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn409WhenUpdateBookmarkWithoutMandatoryField() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);
        final JSONObject jsonObject = extractJsonObjectFromResponse(response);
        jsonObject.remove(FIELD_NAME);

        request.body(jsonObject.toString())
                .put(API_BOOKMARKS)
                .then()
                .statusCode(409)
                .log().all();
    }

    @Test
    void shouldReturn200WhenUpdateMultipleBookmarks() {
        final UUID bookmarkId1 = UUID.randomUUID();
        final ValidatableResponse response1 = createBookmark(bookmarkId1);
        final JSONObject jsonObject1 = extractJsonObjectFromResponse(response1);

        final UUID bookmarkId2 = UUID.randomUUID();
        final ValidatableResponse response2 = createBookmark(bookmarkId2);
        final JSONObject jsonObject2 = extractJsonObjectFromResponse(response2);

        final JSONArray jsonArray = new JSONArray();
        jsonObject1.put(FIELD_NAME, "new name-1");
        jsonObject2.put(FIELD_NAME, "new name-2");
        jsonArray.put(jsonObject1);
        jsonArray.put(jsonObject2);

        request.body(jsonArray.toString())
                .put(API_BOOKMARKS_MULTIPLE)
                .then()
                .statusCode(200)
                .body(FIELD_DOCUMENT_ID, hasItems(documentId.toString()))
                .body(FIELD_ID, hasItems(bookmarkId1.toString(), bookmarkId2.toString()))
                .body(FIELD_NAME, hasItems("new name-1", "new name-2"))
                .log().all();
    }

    @Test
    void shouldReturn400WhenUpdateMultipleBookmarksWithBadRequestPayload() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);
        final JSONObject jsonObject = extractJsonObjectFromResponse(response);
        jsonObject.remove(FIELD_ID);

        final JSONArray jsonArray = new JSONArray().put(jsonObject);

        request.body(jsonArray.toString())
                .put(API_BOOKMARKS_MULTIPLE)
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserUpdateMultipleBookmarks() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);
        final JSONObject jsonObject = extractJsonObjectFromResponse(response);

        final JSONArray jsonArray = new JSONArray().put(jsonObject);

        unAuthenticatedRequest.body(jsonArray.toString())
                .put(API_BOOKMARKS_MULTIPLE)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn409WhenUpdateMultipleBookmarksWithoutMandatoryField() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);
        final JSONObject jsonObject = extractJsonObjectFromResponse(response);
        jsonObject.remove(FIELD_NAME);

        final JSONArray jsonArray = new JSONArray().put(jsonObject);

        request.body(jsonArray.toString())
                .put(API_BOOKMARKS_MULTIPLE)
                .then()
                .statusCode(409)
                .log().all();
    }

    @Test
    void shouldReturn200WhenDeleteBookmarkById() {
        final UUID bookmarkId = UUID.randomUUID();
        final ValidatableResponse response = createBookmark(bookmarkId);
        final JSONObject jsonObject = extractJsonObjectFromResponse(response);
        final String id = jsonObject.getString(FIELD_ID);

        request.delete(String.format(API_BOOKMARKS + "/%s", id))
                .then()
                .statusCode(200)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserDeleteBookmarkById() {
        unAuthenticatedRequest.delete(String.format(API_BOOKMARKS + "/%s", UUID.randomUUID()))
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenDeleteBookmarkByNonExistentId() {
        request.delete(String.format(API_BOOKMARKS + "/%s", UUID.randomUUID()))
                .then()
                .statusCode(200)
                .log().all();
    }

    @Test
    void shouldReturn200WhenDeleteMultipleBookmarks() {
        final UUID bookmarkId1 = UUID.randomUUID();
        final JSONObject jsonObject1 = extractJsonObjectFromResponse(createBookmark(bookmarkId1));

        final UUID bookmarkId2 = UUID.randomUUID();
        final JSONObject jsonObject2 = extractJsonObjectFromResponse(createBookmark(bookmarkId2));

        final JSONArray jsonArray = new JSONArray()
                .put(jsonObject1.get(FIELD_ID))
                .put(jsonObject2.get(FIELD_ID));

        final JSONObject deleteBookmarkRequest = new JSONObject()
                .put(FIELD_DELETED, jsonArray);

        request.body(deleteBookmarkRequest.toString())
                .delete(API_BOOKMARKS_MULTIPLE)
                .then()
                .statusCode(200)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserDeleteMultipleBookmarks() {
        final UUID bookmarkId = UUID.randomUUID();
        final JSONArray jsonArray = new JSONArray().put(bookmarkId);

        final JSONObject deleteBookmarkRequest = new JSONObject()
                .put(FIELD_DELETED, jsonArray);

        unAuthenticatedRequest.body(deleteBookmarkRequest.toString())
                .delete(API_BOOKMARKS_MULTIPLE)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenDeleteMultipleBookmarksWithNonExistentId() {
        final UUID bookmarkId = UUID.randomUUID();
        final JSONArray jsonArray = new JSONArray().put(bookmarkId);

        final JSONObject deleteBookmarkRequest = new JSONObject()
                .put(FIELD_DELETED, jsonArray);

        request.body(deleteBookmarkRequest.toString())
                .delete(API_BOOKMARKS_MULTIPLE)
                .then()
                .statusCode(200)
                .log().all();
    }

    @NotNull
    private ValidatableResponse createBookmark(final UUID bookmarkId) {
        final JSONObject bookmark = createBookmarkRequestPayload(bookmarkId);
        return request.body(bookmark.toString())
                .post(API_BOOKMARKS)
                .then()
                .statusCode(201);
    }

    private JSONObject createBookmarkRequestPayload(final UUID bookmarkId) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(FIELD_ID, bookmarkId);
        jsonObject.put(FIELD_DOCUMENT_ID, documentId);
        jsonObject.put(FIELD_NAME, BOOKMARK_NAME);
        jsonObject.put(FIELD_CREATED_BY, CREATED_BY_USER);
        jsonObject.put(FIELD_PAGE_NUMBER, DEFAULT_PAGE);
        jsonObject.put(FIELD_X_COORD, DEFAULT_COORD);
        jsonObject.put(FIELD_Y_COORD, DEFAULT_COORD);
        jsonObject.put(FIELD_PARENT, UUID.randomUUID().toString());
        jsonObject.put(FIELD_PREVIOUS, UUID.randomUUID().toString());
        return jsonObject;
    }

    @NotNull
    private JSONObject extractJsonObjectFromResponse(final ValidatableResponse response) {
        return response.extract().response().as(JSONObject.class);
    }
}
