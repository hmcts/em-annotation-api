package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import uk.gov.hmcts.reform.em.annotation.testutil.ToggleProperties;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@EnableConfigurationProperties(ToggleProperties.class)
class MetadataScenariosTest extends BaseTest {

    @Autowired
    ToggleProperties toggleProperties;

    @Test
    void shouldReturn201WhenCreateNewMetadata() {
        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        final ValidatableResponse response = createMetadata(documentId);

        response
                .statusCode(201)
                .body("rotationAngle", equalTo(90))
                .body("documentId", equalTo(documentId))
                .header("Location", equalTo("/api/metadata/" + documentId))
                .log().all();
    }

    @Test
    void shouldReturn400WhenCreateNewMetadataWithoutDocumentId() {
        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        final JSONObject metadataPayload = createMetadataPayload(documentId);

        metadataPayload.remove("documentId");

        request
                .body(metadataPayload.toString())
                .post("/api/metadata/")
                .then()
                .statusCode(400)
                .body("fieldErrors", Matchers.hasSize(1))
                .body("fieldErrors[0].field", equalTo("documentId"))
                .body("fieldErrors[0].message", equalTo("NotNull"))
                .log().all();
    }

    @Test
    void shouldReturn400WhenCreateNewMetadataWithoutRotationAngle() {
        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        final JSONObject metadataPayload = createMetadataPayload(documentId);

        metadataPayload.remove("rotationAngle");

        request
                .body(metadataPayload.toString())
                .post("/api/metadata/")
                .then()
                .statusCode(400)
                .body("fieldErrors", Matchers.hasSize(1))
                .body("fieldErrors[0].field", equalTo("rotationAngle"))
                .body("fieldErrors[0].message", equalTo("NotNull"))
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserCreateNewMetadata() {
        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        final JSONObject metadataPayload = createMetadataPayload(documentId);

        unAuthenticatedRequest
                .body(metadataPayload)
                .post("/api/metadata/")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    void shouldReturn200WhenGetMetadataByDocumentId() {
        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        createMetadata(documentId);

        request
                .get("/api/metadata/" + documentId)
                .then()
                .statusCode(200)
                .body("rotationAngle", equalTo(90))
                .body("documentId", equalTo(documentId))
                .log().all();
    }

    @Test
    void shouldReturn204WhenGetMetadataByNonExistentDocumentId() {
        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();

        request
                .get("/api/metadata/" + documentId)
                .then()
                .statusCode(204)
                .log().all();
    }

    @Test
    void shouldReturn401WhenUnAuthenticatedUserGetMetadataByDocumentId() {
        // If the Endpoint Toggles are enabled, continue, if not skip and ignore
        assumeTrue(toggleProperties.isEnableMetadataEndpoint());

        final String documentId = UUID.randomUUID().toString();
        createMetadata(documentId);

        unAuthenticatedRequest
                .get("/api/metadata/" + documentId)
                .then()
                .statusCode(401)
                .log().all();
    }

    public JSONObject createMetadataPayload(final String documentId) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("rotationAngle", 90);
        jsonObject.put("documentId", documentId);

        return jsonObject;
    }

    @NotNull
    private ValidatableResponse createMetadata(final String documentId) {
        final JSONObject metadata = createMetadataPayload(documentId);

        return request
                .body(metadata.toString())
                .post("/api/metadata/")
                .then()
                .statusCode(201)
                .log().all();
    }
}
