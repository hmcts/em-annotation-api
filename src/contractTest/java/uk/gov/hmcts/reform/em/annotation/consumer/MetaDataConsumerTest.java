package uk.gov.hmcts.reform.em.annotation.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.restassured.http.ContentType;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;

class MetaDataConsumerTest extends BaseConsumerTest {

    private static final String METADATA_PROVIDER_NAME = "annotation_api_metadata_provider";
    private static final String METADATA_API_BASE_PATH = "/api/metadata";

    private static final UUID EXAMPLE_DOCUMENT_ID = UUID.fromString("8c53579b-d935-4204-82c8-250329c29d91");
    private static final Integer EXAMPLE_ROTATION_ANGLE = 90;

    @Pact(provider = METADATA_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact createMetaData201(PactDslWithProvider builder) {
        return builder
            .given("metadata can be created for a document")
            .uponReceiving("A request to create metadata")
            .path(METADATA_API_BASE_PATH + "/")
            .method(HttpMethod.POST.toString())
            .headers(getHeaders())
            .body(createMetaDataDsl())
            .willRespondWith()
            .status(HttpStatus.CREATED.value())
            .body(createMetaDataDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createMetaData201", providerName = METADATA_PROVIDER_NAME)
    void testCreateMetaData201(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .body(createMetaDataDsl().getBody().toString())
            .post(mockServer.getUrl() + METADATA_API_BASE_PATH + "/")
            .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Pact(provider = METADATA_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact getMetaData200(PactDslWithProvider builder) {
        String metadataPath = METADATA_API_BASE_PATH + "/" + EXAMPLE_DOCUMENT_ID;
        return builder
            .given("metadata exists for a document")
            .uponReceiving("A request to get metadata for a document")
            .path(metadataPath)
            .method(HttpMethod.GET.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createMetaDataDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getMetaData200", providerName = METADATA_PROVIDER_NAME)
    void testGetMetaData200(MockServer mockServer) {
        String metadataPath = METADATA_API_BASE_PATH + "/" + EXAMPLE_DOCUMENT_ID;
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .get(mockServer.getUrl() + metadataPath)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    private DslPart createMetaDataDsl() {
        return newJsonBody(body -> {
            body
                .integerType("rotationAngle", EXAMPLE_ROTATION_ANGLE)
                .uuid("documentId", EXAMPLE_DOCUMENT_ID);
        }).build();
    }
}