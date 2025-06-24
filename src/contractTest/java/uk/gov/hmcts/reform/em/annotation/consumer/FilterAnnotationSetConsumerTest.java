package uk.gov.hmcts.reform.em.annotation.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDslObject;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;

@Slf4j
@PactTestFor(providerName = "annotation_api_filter_annotation_set_provider")
class FilterAnnotationSetConsumerTest extends BaseConsumerTest {

    private static final String FILTER_ANNOTATION_SET_PROVIDER_NAME = "annotation_api_filter_annotation_set_provider";
    private static final String FILTER_ANNOTATION_SET_API_PATH = "/api/annotation-sets/filter";
    private static final String EXAMPLE_DOCUMENT_ID = "f401727b-5a50-40bb-ac4d-87dc34910b6e";
    private static final UUID EXAMPLE_ANNOTATION_SET_ID = UUID.fromString("4f6fe7a2-b8a6-4f0a-9f7c-8d9e1b0c9b3a");

    private final AnnotationsConsumerTest annotationsConsumerTestHelper = new AnnotationsConsumerTest();

    @Pact(provider = FILTER_ANNOTATION_SET_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact filterAnnotationSetByDocumentId200(PactDslWithProvider builder) {
        return builder
            .given("an annotation set exists for the given document id")
            .uponReceiving("A request to filter an annotation set by document id")
            .path(FILTER_ANNOTATION_SET_API_PATH)
            .method(HttpMethod.GET.toString())
            .matchQuery("documentId", ".+", EXAMPLE_DOCUMENT_ID)
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createAnnotationSetDsl())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "filterAnnotationSetByDocumentId200")
    void testFilterAnnotationSetByDocumentId200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .queryParam("documentId", EXAMPLE_DOCUMENT_ID)
            .get(mockServer.getUrl() + FILTER_ANNOTATION_SET_API_PATH)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    private DslPart createAnnotationSetDsl() {
        return newJsonBody(this::buildAnnotationSetBody).build();
    }

    private void buildAnnotationSetBody(LambdaDslObject body) {
        body
            .uuid("id", EXAMPLE_ANNOTATION_SET_ID)
            .stringType("documentId", EXAMPLE_DOCUMENT_ID);
        buildAuditingFields(body);
        body.eachLike("annotations", annotationsConsumerTestHelper::getLambdaDslObject);
    }
}