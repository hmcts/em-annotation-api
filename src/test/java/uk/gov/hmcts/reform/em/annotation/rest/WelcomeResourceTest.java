package uk.gov.hmcts.reform.em.annotation.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

class WelcomeResourceTest {

    private final WelcomeResource welcomeResource = new WelcomeResource();

    @Test
    void testEndPointResponseCode() {
        ResponseEntity<Map<String,String>> responseEntity = welcomeResource.welcome();

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testEndpointResponseMessage() {
        ResponseEntity<Map<String,String>> responseEntity = welcomeResource.welcome();

        Map<String,String> expectedResponse = new HashMap<>();
        expectedResponse.put("message","Welcome to EM Annotation API!");

        String cacheHeader = responseEntity.getHeaders().getCacheControl();

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals("no-cache", cacheHeader);
        Assertions.assertEquals(expectedResponse, responseEntity.getBody());
    }
}
