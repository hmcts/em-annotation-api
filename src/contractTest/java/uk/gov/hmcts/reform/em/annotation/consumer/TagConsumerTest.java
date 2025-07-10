package uk.gov.hmcts.reform.em.annotation.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
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

@Slf4j
@PactTestFor(providerName = "annotation_api_tag_provider")
class TagConsumerTest extends BaseConsumerTest {

    private static final String TAG_PROVIDER_NAME = "annotation_api_tag_provider";
    private static final String TAGS_API_PATH = "/api/tags/" + EXAMPLE_USER_ID;

    @Pact(provider = TAG_PROVIDER_NAME, consumer = ANNOTATION_CONSUMER)
    public V4Pact getTagsForUser200(PactDslWithProvider builder) {
        return builder
            .given("tags exist for a user")
            .uponReceiving("A request to get all tags for a user")
            .path(TAGS_API_PATH)
            .method(HttpMethod.GET.toString())
            .headers(getHeaders())
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .body(createTagDslList())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getTagsForUser200")
    void testGetTagsForUser200(MockServer mockServer) {
        SerenityRest
            .given()
            .headers(getHeaders())
            .contentType(ContentType.JSON)
            .get(mockServer.getUrl() + TAGS_API_PATH)
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    private DslPart createTagDslList() {
        return LambdaDsl.newJsonArrayMinLike(1, array ->
            array.object(this::buildTagDslObject)
        ).build();
    }

    private void buildTagDslObject(LambdaDslObject body) {
        body
            .stringType("name", "Review")
            .stringType("createdBy", EXAMPLE_USER_ID.toString())
            .stringType("label", "For Review")
            .stringType("color", "ff0000");
    }
}