package uk.gov.hmcts.reform.em.annotation.functional;

import net.serenitybdd.rest.SerenityRest;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;
import uk.gov.hmcts.reform.em.test.s2s.S2sHelper;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

// CHECKSTYLE:OFF: AvoidStarImport - Test Constants class.
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.*;
// CHECKSTYLE:ON: AvoidStarImport

class DocumentDataScenariosTest extends BaseTest {

    private static final String API_DELETE_DATA = "/api/documents/%s/data";

    private final S2sHelper ccdS2sHelper;

    @Autowired
    public DocumentDataScenariosTest(TestUtil testUtil, S2sHelper ccdS2sHelper) {
        super(testUtil);
        this.ccdS2sHelper = ccdS2sHelper;
    }

    @Test
    @DisplayName("Unauthorized Service should receive 403 Forbidden")
    void shouldReturn403WhenUnauthorizedServiceDeletesData() {

        String unauthorizedS2sToken = ccdS2sHelper.getS2sToken();

        SerenityRest.given()
            .baseUri(testUrl)
            .contentType(APPLICATION_JSON_VALUE)
            .header("ServiceAuthorization", unauthorizedS2sToken)
            .delete(String.format(API_DELETE_DATA, UUID.randomUUID()))
            .then()
            .statusCode(403)
            .log().all();
    }

    @Test
    @DisplayName("Missing ServiceAuthorization header should return 401 Unauthorized")
    void shouldReturn401WhenS2SHeaderIsMissing() {
        testUtil.validAuthRequestWithEmptyS2SAuth()
            .baseUri(testUrl)
            .contentType(APPLICATION_JSON_VALUE)
            .delete(String.format(API_DELETE_DATA, UUID.randomUUID()))
            .then()
            .statusCode(401);
    }

    @Test
    @DisplayName("Invalid ServiceAuthorization token should return 401 Unauthorized")
    void shouldReturn401WhenS2STokenIsInvalid() {
        testUtil.invalidS2SAuth()
            .baseUri(testUrl)
            .contentType(APPLICATION_JSON_VALUE)
            .delete(String.format(API_DELETE_DATA, UUID.randomUUID()))
            .then()
            .statusCode(401);
    }

    @Test
    @DisplayName("Authorized Service can delete full document hierarchy")
    void shouldReturn204WhenDeletingFullDocumentHierarchy() {
        final UUID documentId = UUID.randomUUID();
        final UUID annotationSetId = UUID.randomUUID();
        final UUID annotationId = UUID.randomUUID();
        final UUID bookmarkId = UUID.randomUUID();

        createAnnotationSet(annotationSetId, documentId);
        createAnnotationWithDetails(annotationId, annotationSetId);
        createBookmark(bookmarkId, documentId);

        verifyAnnotationSetExists(annotationSetId);
        verifyBookmarkExists(bookmarkId, documentId);

        request
            .delete(String.format(API_DELETE_DATA, documentId))
            .then()
            .statusCode(204);

        verifyAnnotationSetIsDeleted(annotationSetId);
        verifyBookmarksAreDeleted(documentId);
    }

    @Test
    @DisplayName("Authorized Service can delete Bookmarks")
    void shouldReturn204WhenDeletingDocumentWithOnlyBookmarks() {
        final UUID documentId = UUID.randomUUID();
        final UUID bookmarkId = UUID.randomUUID();

        createBookmark(bookmarkId, documentId);
        verifyBookmarkExists(bookmarkId, documentId);

        request
            .delete(String.format(API_DELETE_DATA, documentId))
            .then()
            .statusCode(204);

        verifyBookmarksAreDeleted(documentId);
    }

    @Test
    @DisplayName("Delete document that has no data")
    void shouldReturn204WhenDeletingNonExistentDocument() {
        request
            .delete(String.format(API_DELETE_DATA, UUID.randomUUID()))
            .then()
            .statusCode(204);
    }

    @Test
    @DisplayName("Invalid UUID path variable should return 400 Bad Request")
    void shouldReturn400WhenDocumentIdIsInvalid() {
        request
            .delete(String.format(API_DELETE_DATA, "i_am_not_a_uuid"))
            .then()
            .statusCode(400)
            .log().all();
    }

    private void verifyAnnotationSetExists(UUID annotationSetId) {
        request.get(API_ANNOTATION_SETS + "/" + annotationSetId)
            .then().statusCode(200).body(FIELD_ID, equalTo(annotationSetId.toString()));
    }

    private void verifyBookmarkExists(UUID bookmarkId, UUID documentId) {
        request.get(String.format(API_BASE + BOOKMARKS, documentId))
            .then().statusCode(200).body(FIELD_ID, Matchers.hasItem(bookmarkId.toString()));
    }

    private void verifyAnnotationSetIsDeleted(UUID annotationSetId) {
        request.get(API_ANNOTATION_SETS + "/" + annotationSetId).then().statusCode(204);
    }

    private void verifyBookmarksAreDeleted(UUID docId) {
        request.get(String.format(API_BASE + BOOKMARKS, docId)).then().statusCode(204);
    }

    private void createAnnotationSet(final UUID annotationSetId, final UUID documentId) {
        final JSONObject annotationSet = new JSONObject();
        annotationSet.put(FIELD_DOCUMENT_ID, documentId);
        annotationSet.put(FIELD_ID, annotationSetId.toString());
        annotationSet.put(FIELD_ANNOTATIONS, new JSONArray());
        request.body(annotationSet.toString()).post(API_ANNOTATION_SETS).then().statusCode(201);
    }

    private void createAnnotationWithDetails(UUID annotationId, UUID annotationSetId) {
        final JSONObject annotation = new JSONObject();
        annotation.put(FIELD_ANNOTATION_SET_ID, annotationSetId.toString());
        annotation.put(FIELD_ID, annotationId.toString());
        annotation.put(FIELD_ANNOTATION_TYPE, VALUE_HIGHLIGHT);
        annotation.put(FIELD_PAGE, 1);
        annotation.put(FIELD_COLOR, VALUE_COLOR);

        final JSONArray comments = new JSONArray();
        final JSONObject comment = new JSONObject();
        comment.put(FIELD_CONTENT, VALUE_TEXT);
        comment.put(FIELD_ANNOTATION_ID, annotationId);
        comment.put(FIELD_ID, UUID.randomUUID().toString());
        comments.put(0, comment);
        annotation.put(FIELD_COMMENTS, comments);

        final JSONArray rectangles = new JSONArray();
        final JSONObject rectangle = new JSONObject();
        rectangle.put(FIELD_ID, UUID.randomUUID().toString());
        rectangle.put(FIELD_ANNOTATION_ID, annotationId);
        rectangle.put("x", 0f);
        rectangle.put("y", 0f);
        rectangle.put("width", 10f);
        rectangle.put("height", 11f);
        rectangles.put(0, rectangle);
        annotation.put(FIELD_RECTANGLES, rectangles);

        request.body(annotation.toString()).post(API_ANNOTATIONS).then().statusCode(201);
    }

    private void createBookmark(final UUID bookmarkId, final UUID docId) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(FIELD_ID, bookmarkId);
        jsonObject.put(FIELD_DOCUMENT_ID, docId);
        jsonObject.put(FIELD_NAME, BOOKMARK_NAME);
        jsonObject.put(FIELD_PAGE_NUMBER, DEFAULT_PAGE);
        jsonObject.put(FIELD_X_COORD, DEFAULT_COORD);
        jsonObject.put(FIELD_Y_COORD, DEFAULT_COORD);
        jsonObject.put(FIELD_PARENT, UUID.randomUUID().toString());
        jsonObject.put(FIELD_PREVIOUS, UUID.randomUUID().toString());
        request.body(jsonObject.toString()).post(API_BOOKMARKS).then().statusCode(201);
    }
}