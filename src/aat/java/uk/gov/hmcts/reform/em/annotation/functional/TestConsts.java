package uk.gov.hmcts.reform.em.annotation.functional;

import org.springframework.http.MediaType;

public final class TestConsts {
    // 🔹 API Endpoints
    public static final String API_ANNOTATIONS = "/api/annotations";
    public static final String API_ANNOTATION_SETS = "/api/annotation-sets";
    public static final String BOOKMARKS = "/%s/bookmarks";
    public static final String API_BASE = "/api";
    public static final String API_BOOKMARKS = API_BASE + "/bookmarks";
    public static final String API_BOOKMARKS_MULTIPLE = API_BASE + "/bookmarks_multiple";
    public static final String API_COMMENTS = "/api/comments";
    public static final String API_FILTER = API_ANNOTATION_SETS + "/filter";
    public static final String API_METADATA = "/api/metadata/";
    public static final String API_RECTANGLES = "/api/rectangles";
    public static final String API_RECTANGLES_ID = API_RECTANGLES + "/";

    // 🔹 JSON field keys
    public static final String FIELD_ID = "id";
    public static final String FIELD_PAGE = "page";
    public static final String FIELD_COLOR = "color";
    public static final String FIELD_DOCUMENT_ID = "documentId";
    public static final String FIELD_ANNOTATION_ID = "annotationId";
    public static final String FIELD_ANNOTATION_SET_ID = "annotationSetId";
    public static final String FIELD_ANNOTATION_TYPE = "annotationType";
    public static final String FIELD_COMMENTS = "comments";
    public static final String FIELD_RECTANGLES = "rectangles";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_X = "x";
    public static final String FIELD_Y = "y";
    public static final String FIELD_WIDTH = "width";
    public static final String FIELD_HEIGHT = "height";
    public static final String FIELD_CASE_ID = "caseId";
    public static final String FIELD_JURISDICTION = "jurisdiction";
    public static final String FIELD_COMMENT_HEADER = "commentHeader";
    public static final String FIELD_ANNOTATIONS = "annotations";
    public static final String HEADER_LOCATION = "Location";
    public static final String VALUE_HIGHLIGHT = "highlight";
    public static final String VALUE_COLOR = "d1d1d1";
    public static final String VALUE_TEXT = "text";

    public static final String FIELD_NAME = "name";
    public static final String FIELD_LABEL = "label";
    public static final String FIELD_TAGS = "tags";
    public static final String FIELD_CREATED_BY = "createdBy";
    public static final String FIELD_CREATED_BY_DETAILS = "createdByDetails";
    public static final String FIELD_CREATED_DATE = "createdDate";
    public static final String FIELD_LAST_MODIFIED_BY_DETAILS = "lastModifiedByDetails";
    public static final String FIELD_LAST_MODIFIED_DATE = "lastModifiedDate";
    public static final String FIELD_EMAIL = "email";
    public static final String LAST_MODIFIED_BY_DETAILS_EMAIL_PATH =
        String.format("%s.%s", FIELD_LAST_MODIFIED_BY_DETAILS, FIELD_EMAIL);
    public static final String CREATED_BY_DETAILS_EMAIL_PATH =
        String.format("%s.%s", FIELD_CREATED_BY_DETAILS, FIELD_EMAIL);
    public static final String FIELD_PAGE_NUMBER = "pageNumber";
    public static final String FIELD_X_COORD = "xCoordinate";
    public static final String FIELD_Y_COORD = "yCoordinate";
    public static final String FIELD_PARENT = "parent";
    public static final String FIELD_PREVIOUS = "previous";
    public static final String FIELD_DELETED = "deleted";
    public static final String FIELD_ROTATION_ANGLE = "rotationAngle";
    public static final String FIELD_ERRORS = "fieldErrors";

    public static final String CREATED_BY_USER = "user";
    public static final String BOOKMARK_NAME = "Bookmark for test";
    public static final float DEFAULT_COORD = 100f;
    public static final int DEFAULT_PAGE = 1;

    public static final String DEFAULT_CONTENT = "text";
    public static final String UPDATED_CONTENT = "updated text";
    public static final String NEW_CONTENT = "new text";
    public static final String HIGHLIGHT = "highlight";
    public static final String COLOR_CODE = "d1d1d1";



    // 🔹 Common values
    public static final String COLOR_DEFAULT = "d1d1d1";
    public static final String COLOR_UPDATED = "f1f1f1";
    public static final String COLOR_SECOND_UPDATE = "e1e1e1";
    public static final String ANNOTATION_TYPE_HIGHLIGHT = "highlight";
    public static final String COMMENT_TEXT = "text";
    public static final String LOCATION_HEADER = "Location";
    public static final String PUBLIC_LAW = "PUBLICLAW";

    public static final int DEFAULT_ROTATION_ANGLE = 90;
    public static final String VALIDATION_NOT_NULL = "NotNull";

    public static final String FIELD = "field";
    public static final String MESSAGE = "message";

    public static final float DEFAULT_X = 1f;
    public static final float DEFAULT_Y = 2f;
    public static final float DEFAULT_WIDTH = 10f;
    public static final float DEFAULT_HEIGHT = 11f;

    public static final String DEFAULT_ANNOTATION_TYPE = "highlight";
    public static final String DEFAULT_COLOR = "d1d1d1";

    public static final String HEADER_LOCATION_TEMPLATE = API_RECTANGLES_ID;

    public static final String CONTENT_TYPE_JSON = MediaType.APPLICATION_JSON_VALUE;

    public static final String ANNOTATION_TEST_USER_EMAIL = "emAnnotationTestUser@test.local";

    // === HTTP Status Codes ===
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_OK = 200;
    public static final int STATUS_NO_CONTENT = 204;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;

    private TestConsts() {
    }
}
