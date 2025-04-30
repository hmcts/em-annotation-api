package uk.gov.hmcts.reform.em.annotation.consumer;

import au.com.dius.pact.consumer.dsl.LambdaDslObject;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseConsumerTest {

    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    public static final String AUTH_TOKEN = "Bearer someAuthorizationToken";
    public static final String SERVICE_AUTH_TOKEN = "Bearer someServiceAuthorizationToken";
    protected static final String ANNOTATION_CONSUMER = "annotation_api";
    protected static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    protected static final UUID EXAMPLE_USER_ID = UUID.fromString("c38fd29e-fa2e-43d4-a599-2d3f2908565b");

    public Map<String, String> getHeaders() {
        return Map.of(
            SERVICE_AUTHORIZATION, SERVICE_AUTH_TOKEN,
            AUTHORIZATION, AUTH_TOKEN,
            "Content-Type", "application/json"
        );
    }

    protected void buildIdamDetails(LambdaDslObject details) {
        details
            .stringType("forename", "Test")
            .stringType("surname", "User")
            .stringType("email", "test.user.annotations@example.com");
    }

    protected void buildAuditingFields(LambdaDslObject body) {
        body
            .uuid("createdBy", EXAMPLE_USER_ID)
            .datetime("createdDate", DATE_TIME_FORMAT)
            .object("createdByDetails", this::buildIdamDetails)
            .uuid("lastModifiedBy", EXAMPLE_USER_ID)
            .datetime("lastModifiedDate", DATE_TIME_FORMAT)
            .object("lastModifiedByDetails", this::buildIdamDetails);
    }
}