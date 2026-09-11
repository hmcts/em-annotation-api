package uk.gov.hmcts.reform.em.annotation.smoke;

import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(
    classes = SmokeTest.TestConfig.class,
    initializers = ConfigDataApplicationContextInitializer.class
)
@ExtendWith({SerenityJUnit5Extension.class, SpringExtension.class})
@WithTags({@WithTag("testType:Smoke")})
class SmokeTest {

    private static final String MESSAGE = "{\"message\":\"Welcome to EM Annotation API!\"}";

    @TestConfiguration
    static class TestConfig {
    }

    @Value("${test.url}")
    private String testUrl;

    @Test
    void testHealthEndpoint() {

        SerenityRest.useRelaxedHTTPSValidation();

        String response =
                SerenityRest
                        .given()
                        .baseUri(testUrl)
                        .get("/")
                        .then()
                        .statusCode(200)
                        .extract()
                        .asString();

        assertEquals(MESSAGE, response);


    }
}
