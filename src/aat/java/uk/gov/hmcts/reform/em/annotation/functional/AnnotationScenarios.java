package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.em.annotation.Application;
import uk.gov.hmcts.reform.em.annotation.testutil.Env;
import uk.gov.hmcts.reform.em.annotation.testutil.IdamHelper;
import uk.gov.hmcts.reform.em.annotation.testutil.S2sHelper;

import static org.hamcrest.CoreMatchers.*;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, properties = "SpringBootTest")
@TestPropertySource(locations = "classpath:application-aat.yaml")
@ActiveProfiles("aat")
public class AnnotationScenarios {

    @Autowired
    private IdamHelper idamHelper;

    @Autowired
    private S2sHelper s2sHelper;

    private String idamAuth;
    private String s2sAuth;

    @Before
    public void setup() {
        idamAuth = idamHelper.getIdamToken();
        s2sAuth = s2sHelper.getS2sToken();

        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    public void testGetAnnotationSets() throws Exception {
        RestAssured.given()
            .header("Authorization", idamAuth)
            .header("ServiceAuthorization", s2sAuth)
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .request("GET", Env.getTestUrl() + "/api/annotation-sets")
            .then()
            .statusCode(200);

    }

    @Test
    public void testCreateAnnotationSetAndAnnotationsThenUpdateThenDelete() throws Exception {

        JSONObject jsonObject = new JSONObject();

        UUID newAnnotationSetId = UUID.randomUUID();
        jsonObject.put("documentId", UUID.randomUUID().toString());
        jsonObject.put("id", newAnnotationSetId.toString());

        RestAssured.given()
            .header("Authorization", idamAuth)
            .header("ServiceAuthorization", s2sAuth)
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(jsonObject.toString())
            .request("POST", Env.getTestUrl() + "/api/annotation-sets")
            .then()
            .statusCode(201);

        UUID annotationId = UUID.randomUUID();
        JSONObject createAnnotations = new JSONObject();
        createAnnotations.put("annotationSetId", newAnnotationSetId);
        createAnnotations.put("id", annotationId);
        createAnnotations.put("annotationType", "highlight");
        createAnnotations.put("page", 1);
        createAnnotations.put("color", "d1d1d1");

        JSONArray comments = new JSONArray();
        JSONObject comment = new JSONObject();
        comment.put("content", "text");
        comment.put("annotationId", annotationId);
        comment.put("id", UUID.randomUUID().toString());
        comments.put(0, comment);
        createAnnotations.put("comments", comments);

        JSONArray rectangles = new JSONArray();
        JSONObject rectangle = new JSONObject();
        rectangle.put("id", UUID.randomUUID().toString());
        rectangle.put("annotationId", annotationId);
        rectangle.put("x", 0f);
        rectangle.put("y", 0f);
        rectangle.put("width", 10f);
        rectangle.put("height", 11f);
        rectangles.put(0, rectangle);
        createAnnotations.put("rectangles", rectangles);

        RestAssured.given()
            .header("Authorization", idamAuth)
            .header("ServiceAuthorization", s2sAuth)
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(createAnnotations)
                .request("POST", Env.getTestUrl() + "/api/annotations")
                .then()
                .statusCode(201)
                .body("id", equalTo(annotationId.toString()))
                .body("rectangles", Matchers.hasSize(1))
                .body("rectangles[0].x", is(0f))
                .body("rectangles[0].y", is(0f))
                .body("rectangles[0].width", is(10f))
                .body("rectangles[0].height", is(11f))
                .body("comments[0].content", is("text"))
                .body("comments", Matchers.hasSize(1));

        comment.put("content", "text2");
        rectangle.put("height", 13f);

        RestAssured.given()
            .header("Authorization", idamAuth)
            .header("ServiceAuthorization", s2sAuth)
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(createAnnotations)
                .request("PUT", Env.getTestUrl() + "/api/annotations")
                .then()
                .statusCode(200)
                .body("id", equalTo(annotationId.toString()))
                .body("rectangles", Matchers.hasSize(1))
                .body("rectangles[0].x", is(0f))
                .body("rectangles[0].y", is(0f))
                .body("rectangles[0].width", is(10f))
                .body("rectangles[0].height", is(13f))
                .body("comments[0].content", is("text2"))
                .body("comments", Matchers.hasSize(1));

        RestAssured.given()
            .header("Authorization", idamAuth)
            .header("ServiceAuthorization", s2sAuth)
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .request("DELETE", Env.getTestUrl() + "/api/annotations/" + annotationId.toString() )
                .then()
                .statusCode(200);

        RestAssured.given()
            .header("Authorization", idamAuth)
            .header("ServiceAuthorization", s2sAuth)
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .request("GET", Env.getTestUrl() + "/api/annotations/" + annotationId.toString() )
                .then()
                .statusCode(404);


    }


}
