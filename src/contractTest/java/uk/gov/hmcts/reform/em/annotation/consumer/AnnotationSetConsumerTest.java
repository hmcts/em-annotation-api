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

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;

@Slf4j
@PactTestFor(providerName = "annotation_api_annotation_set_provider")
class AnnotationSetConsumerTest extends BaseConsumerTest {

    private static final String ANNOTATION_SET_PROVIDER_NAME = "annotation_api_annotation_set_provider";
    private static final String ANNOTATION_SET_API_BASE_PATH = "/api/annotation-sets";

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


    private DslPart createAnnotationSetDsl() {
        return newJsonBody(this::buildAnnotationSetBody).build();
    }

    private void buildAnnotationSetBody(LambdaDslObject body) {
        body
            .uuid("id", UUID.fromString("4f6fe7a2-b8a6-4f0a-9f7c-8d9e1b0c9b3a"))
            .stringType("documentId", "f401727b-5a50-40bb-ac4d-87dc34910b6e");
        buildAuditingFields(body);
        body.eachLike("annotations", annotationsConsumerTestHelper::getLambdaDslObject);
    }
}