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
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.API_METADATA;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.DEFAULT_ROTATION_ANGLE;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.FIELD;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.FIELD_DOCUMENT_ID;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.FIELD_ERRORS;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.FIELD_ROTATION_ANGLE;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.HEADER_LOCATION;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.MESSAGE;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.STATUS_BAD_REQUEST;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.STATUS_CREATED;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.STATUS_NO_CONTENT;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.STATUS_OK;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.STATUS_UNAUTHORIZED;
import static uk.gov.hmcts.reform.em.annotation.functional.TestConsts.VALIDATION_NOT_NULL;

@EnableConfigurationProperties(ToggleProperties.class)
class MetadataScenariosTest extends BaseTest {

    private final ToggleProperties toggleProperties;

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
