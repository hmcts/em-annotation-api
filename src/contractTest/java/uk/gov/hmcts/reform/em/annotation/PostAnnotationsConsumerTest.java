package uk.gov.hmcts.reform.em.annotation;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostAnnotationsConsumerTest {

    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    public static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    public static final String SERVICE_AUTH_TOKEN = "Bearer someServiceAuthorizationToken";

    @Pact(provider = "annotation_api", consumer = "annotation_api")
    public V4Pact createAnnotation200(PactDslWithProvider builder) {
        return builder
            .given("annotation is created successfully")
            .uponReceiving("A request to create an annotation")
            .path("/api/annotations")
            .method(HttpMethod.POST.toString())
            .headers(getHeaders())
            .body(createAnnotationRequest())
            .willRespondWith()
            .status(HttpStatus.CREATED.value())
            .body(createAnnotationResponse())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createAnnotation200")
    void testCreateAnnotation200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .body(createAnnotationRequest().getBody().toString())
            .post(mockServer.getUrl() + "/api/annotations")
            .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    public Map<String, String> getHeaders() {
        return Map.of(
            SERVICE_AUTHORIZATION, SERVICE_AUTH_TOKEN,
            AUTHORIZATION, AUTH_TOKEN,
            "Content-Type", "application/json"
        );
    }

    private DslPart createAnnotationRequest() {
        return newJsonBody(o -> o
            .stringType("id", "4d4b6fgh-c91f-433f-92ac-e456ae34f72a")
            .stringType("annotationType", "Test Annotation")
            .numberType("page", 1)
            .stringType("color", "red")
            .stringType("annotationSetId", "123e4567-e89b-12d3-a456-426614174000")
            .array("comments", comments -> comments
                .object(comment -> comment
                    .stringType("id", "123e4567-e89b-12d3-a456-426614174001")
                    .stringType("content", "Sample comment")
                )
            )
            .array("tags", tags -> tags
                .object(tag -> tag
                    .stringType("id", "123e4567-e89b-12d3-a456-426614174002")
                    .stringType("name", "Sample tag")
                )
            )
            .array("rectangles", rectangles -> rectangles
                .object(rectangle -> rectangle
                    .stringType("id", "123e4567-e89b-12d3-a456-426614174003")
                    .numberType("x", 10)
                    .numberType("y", 20)
                    .numberType("width", 30)
                    .numberType("height", 40)
                )
            )
            .stringType("documentId", "123e4567-e89b-12d3-a456-426614174004")
            .stringType("caseId", "12345")
            .stringType("jurisdiction", "Test Jurisdiction")
            .stringType("commentHeader", "Sample header")
        ).build();
    }


    private DslPart createAnnotationResponse() {
        return newJsonBody(o -> o
            .stringType("id", "4d4b6fgh-c91f-433f-92ac-e456ae34f72a")
            .stringType("annotationType", "Test Annotation")
            .numberType("page", 1)
            .stringType("color", "red")
            .stringType("annotationSetId", "123e4567-e89b-12d3-a456-426614174000")
            .array("comments", comments -> comments
                .object(comment -> comment
                    .stringType("id", "123e4567-e89b-12d3-a456-426614174001")
                    .stringType("content", "Sample comment")
                )
            )
            .array("tags", tags -> tags
                .object(tag -> tag
                    .stringType("id", "123e4567-e89b-12d3-a456-426614174002")
                    .stringType("name", "Sample tag")
                )
            )
            .array("rectangles", rectangles -> rectangles
                .object(rectangle -> rectangle
                    .stringType("id", "123e4567-e89b-12d3-a456-426614174003")
                    .numberType("x", 10)
                    .numberType("y", 20)
                    .numberType("width", 30)
                    .numberType("height", 40)
                )
            )
            .stringType("documentId", "123e4567-e89b-12d3-a456-426614174004")
            .stringType("caseId", "12345")
            .stringType("jurisdiction", "Test Jurisdiction")
            .stringType("commentHeader", "Sample header")
        ).build();
    }
}