package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;
import uk.gov.hmcts.reform.em.annotation.testutil.ToggleProperties;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@EnableConfigurationProperties(ToggleProperties.class)
class MetadataScenariosTest extends BaseTest {

    private final ToggleProperties toggleProperties;

    // === Common API paths ===
    private static final String API_METADATA = "/api/metadata/";

    // === JSON field names ===
    private static final String FIELD_ROTATION_ANGLE = "rotationAngle";
    private static final String FIELD_DOCUMENT_ID = "documentId";
    private static final String FIELD_ERRORS = "fieldErrors";
    private static final String FIELD = "field";
    private static final String MESSAGE = "message";

    // === JSON field values ===
    private static final int DEFAULT_ROTATION_ANGLE = 90;
    private static final String VALIDATION_NOT_NULL = "NotNull";

    // === Headers ===
    private static final String HEADER_LOCATION = "Location";

    // === HTTP Status Codes ===
    private static final int STATUS_CREATED = 201;
    private static final int STATUS_BAD_REQUEST = 400;
    private static final int STATUS_UNAUTHORIZED = 401;
    private static final int STATUS_OK = 200;
    private static final int STATUS_NO_CONTENT = 204;

    @Autowired
    public MetadataScenariosTest(TestUtil testUtil, ToggleProperties toggleProperties) {
        super(testUtil);
        this.toggleProperties = toggleProperties;
    }

    @Test
    void shouldReturn201WhenCreateNewMetadata() {
        assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        final ValidatableResponse response = createMetadata(documentId);

        response
                .statusCode(STATUS_CREATED)
                .body(FIELD_ROTATION_ANGLE, equalTo(DEFAULT_ROTATION_ANGLE))
                .body(FIELD_DOCUMENT_ID, equalTo(documentId))
                .header(HEADER_LOCATION, equalTo(API_METADATA + documentId))
                .log().all();
    }

    @Test
    void shouldReturn400WhenCreateNewMetadataWithoutDocumentId() {
        assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        final JSONObject metadataPayload = createMetadataPayload(documentId);
        metadataPayload.remove(FIELD_DOCUMENT_ID);

        request
                .body(metadataPayload.toString())
                .post(API_METADATA)
                .then()
                .statusCode(STATUS_BAD_REQUEST)
                .body(FIELD_ERRORS, Matchers.hasSize(1))
                .body(FIELD_ERRORS + "[0]." + FIELD, equalTo(FIELD_DOCUMENT_ID))
                .body(FIELD_ERRORS + "[0]." + MESSAGE, equalTo(VALIDATION_NOT_NULL))
                .log().all();
    }

    @Test
    void shouldReturn400WhenCreateNewMetadataWithoutRotationAngle() {
        assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        final JSONObject metadataPayload = createMetadataPayload(documentId);
        metadataPayload.remove(FIELD_ROTATION_ANGLE);

        request
                .body(metadataPayload.toString())
                .post(API_METADATA)
                .then()
                .statusCode(STATUS_BAD_REQUEST)
                .body(FIELD_ERRORS, Matchers.hasSize(1))
                .body(FIELD_ERRORS + "[0]." + FIELD, equalTo(FIELD_ROTATION_ANGLE))
                .body(FIELD_ERRORS + "[0]." + MESSAGE, equalTo(VALIDATION_NOT_NULL))
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserCreateNewMetadata() {
        assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        final JSONObject metadataPayload = createMetadataPayload(documentId);

        unAuthenticatedRequest
                .body(metadataPayload)
                .post(API_METADATA)
                .then()
                .statusCode(STATUS_UNAUTHORIZED)
                .log().all();
    }

    @Test
    void shouldReturn200WhenGetMetadataByDocumentId() {
        assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        createMetadata(documentId);

        request
                .get(API_METADATA + documentId)
                .then()
                .statusCode(STATUS_OK)
                .body(FIELD_ROTATION_ANGLE, equalTo(DEFAULT_ROTATION_ANGLE))
                .body(FIELD_DOCUMENT_ID, equalTo(documentId))
                .log().all();
    }

    @Test
    void shouldReturn204WhenGetMetadataByNonExistentDocumentId() {
        assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();

        request
                .get(API_METADATA + documentId)
                .then()
                .statusCode(STATUS_NO_CONTENT)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserGetMetadataByDocumentId() {
        assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        createMetadata(documentId);

        unAuthenticatedRequest
                .get(API_METADATA + documentId)
                .then()
                .statusCode(STATUS_UNAUTHORIZED)
                .log().all();
    }

    @NotNull
    private JSONObject createMetadataPayload(final String documentId) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(FIELD_ROTATION_ANGLE, DEFAULT_ROTATION_ANGLE);
        jsonObject.put(FIELD_DOCUMENT_ID, documentId);
        return jsonObject;
    }

    @NotNull
    private ValidatableResponse createMetadata(final String documentId) {
        final JSONObject metadata = createMetadataPayload(documentId);

        return request
                .body(metadata.toString())
                .post(API_METADATA)
                .then()
                .statusCode(STATUS_CREATED)
                .log().all();
    }
}
