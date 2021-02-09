package uk.gov.hmcts.reform.em.annotation.functional;

import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.em.EmTestConfig;
import uk.gov.hmcts.reform.em.annotation.testutil.TestUtil;
import uk.gov.hmcts.reform.em.test.retry.RetryRule;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = {TestUtil.class, EmTestConfig.class})
@TestPropertySource(value = "classpath:application.yml")
@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
public class CommentScenarios {

    @Autowired
    private TestUtil testUtil;

    @Value("${test.url}")
    private String testUrl;

    @Rule
    public RetryRule retryRule = new RetryRule(3);

    private RequestSpecification request;
    private RequestSpecification unAuthenticatedRequest;

    @Before
    public void setupRequestSpecification() {
        request = testUtil
                .authRequest()
                .baseUri(testUrl)
                .contentType(APPLICATION_JSON_VALUE);

        unAuthenticatedRequest = testUtil
                .unauthenticatedRequest()
                .baseUri(testUrl)
                .contentType(APPLICATION_JSON_VALUE);
    }

    @Test
    public void shouldReturn201WhenCreateNewComment() {
        final UUID newAnnotationSetId = createAnnotationSet();
        final UUID annotationId = createAnnotation(newAnnotationSetId);

        final String commentId = UUID.randomUUID().toString();
        createComment(annotationId, commentId);
    }

    @Test
    public void shouldReturn400WhenCreateNewComment() {
        JSONObject comment = new JSONObject();
        comment.put("content", "text");
        comment.put("annotationId", UUID.randomUUID());

        request
                .body(comment.toString())
                .post("/api/comments")
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenCreateNewComment() {
        final UUID newAnnotationSetId = createAnnotationSet();
        final UUID annotationId = createAnnotation(newAnnotationSetId);
        final String commentId = UUID.randomUUID().toString();
        JSONObject comment = new JSONObject();
        comment.put("id", commentId);
        comment.put("content", "text");
        comment.put("annotationId", annotationId);

        unAuthenticatedRequest
                .body(comment.toString())
                .post("/api/comments")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn500WhenCreateNewComment() {
        JSONObject comment = new JSONObject();
        comment.put("id", UUID.randomUUID());
        comment.put("content", "text");
        comment.put("annotationId", UUID.randomUUID());

        request
                .body(comment.toString())
                .post("/api/comments")
                .then()
                .statusCode(500)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenGetCommentById() {
        final UUID newAnnotationSetId = createAnnotationSet();
        final UUID annotationId = createAnnotation(newAnnotationSetId);

        final String commentId = UUID.randomUUID().toString();
        createComment(annotationId, commentId);

        request
                .get("/api/comments/" + commentId)
                .then()
                .statusCode(200)
                .body("id", equalTo(commentId))
                .body("content", equalTo("text"))
                .body("annotationId", equalTo(annotationId.toString()))
                .log().all();
    }

    @Test
    public void shouldReturn404WhenGetCommentNotFoundById() {
        final String commentId = UUID.randomUUID().toString();
        request
                .get("/api/comments/" + commentId)
                .then()
                .statusCode(404)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenGetCommentById() {
        final String commentId = UUID.randomUUID().toString();
        unAuthenticatedRequest
                .get("/api/comments/" + commentId)
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenGetAllComments() {

        final UUID newAnnotationSetId = createAnnotationSet();
        final UUID annotationId = createAnnotation(newAnnotationSetId);

        final String commentId = UUID.randomUUID().toString();
        createComment(annotationId, commentId);

        request
                .get("/api/comments")
                .then()
                .statusCode(200)
                .body("size()", Matchers.greaterThanOrEqualTo(1))
                .body("id", hasItem(commentId))
                .body("content", hasItem("text"))
                .body("annotationId", hasItem(annotationId.toString()))
                .log().all();
    }


    @Test
    public void shouldReturn401WhenGetAllComments() {
        unAuthenticatedRequest
                .get("/api/comments")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenGetAllCommentsResponseIsEmpty() {
        final JsonPath jsonPath =
                request
                        .get("/api/comments")
                        .then()
                        .log().all()
                        .extract()
                        .response()
                        .getBody()
                        .jsonPath();

        jsonPath.getList("id").forEach(id -> request.delete("/api/comments/" + id));

        request
                .get("/api/comments")
                .then()
                .statusCode(200)
                .log().all()
                .body("size()", is(0));
    }

    @Test
    public void shouldReturn200WhenUpdateComment() {
        final UUID newAnnotationSetId = createAnnotationSet();
        final UUID annotationId = createAnnotation(newAnnotationSetId);

        final String commentId = UUID.randomUUID().toString();
        final JSONObject comment = createComment(annotationId, commentId);

        comment.put("content", "updated text");

        request
                .body(comment.toString())
                .put("/api/comments")
                .then()
                .statusCode(200)
                .body("id", equalTo(commentId))
                .body("content", equalTo("updated text"))
                .body("annotationId", equalTo(annotationId.toString()))
                .log().all();
    }

    @Test
    public void shouldReturn500WhenUpdateComment() {
        JSONObject comment = new JSONObject();
        comment.put("id", UUID.randomUUID());
        comment.put("annotationId", UUID.randomUUID());
        comment.put("content", "updated text");

        request
                .body(comment.toString())
                .put("/api/comments")
                .then()
                .statusCode(500)
                .log().all();
    }

    @Test
    public void shouldReturn400WhenUpdateComment() {
        JSONObject comment = new JSONObject();
        comment.put("content", "text");
        comment.put("annotationId", UUID.randomUUID());

        request
                .body(comment.toString())
                .put("/api/comments")
                .then()
                .statusCode(400)
                .log().all();
    }

    @Test
    public void shouldReturn401WhenUpdateComment() {
        JSONObject comment = new JSONObject();
        comment.put("content", "text");
        comment.put("annotationId", UUID.randomUUID());

        unAuthenticatedRequest
                .body(comment.toString())
                .put("/api/comments")
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenDeleteCommentById() {
        final UUID newAnnotationSetId = createAnnotationSet();
        final UUID annotationId = createAnnotation(newAnnotationSetId);

        final String commentId = UUID.randomUUID().toString();

        createComment(annotationId, commentId);

        deleteCommentById(commentId);
    }

    @Test
    public void shouldReturn401WhenDeleteComment() {
        unAuthenticatedRequest
                .delete("/api/comments/" + UUID.randomUUID())
                .then()
                .statusCode(401)
                .log().all();
    }

    @Test
    public void shouldReturn200WhenTryToUpdateCommentAfterItHasBeenDeleted() {
        final UUID newAnnotationSetId = createAnnotationSet();
        final UUID annotationId = createAnnotation(newAnnotationSetId);

        final String commentId = UUID.randomUUID().toString();
        final JSONObject comment = createComment(annotationId, commentId);

        deleteCommentById(commentId);

        request
                .body(comment.toString())
                .put("/api/comments")
                .then()
                .statusCode(200)
                .body("id", equalTo(commentId))
                .body("content", equalTo("text"))
                .body("annotationId", equalTo(annotationId.toString()))
                .log().all();
    }

    private void deleteCommentById(String commentId) {
        request
                .delete("/api/comments/" + commentId)
                .then()
                .statusCode(200)
                .log().all();
    }

    @NotNull
    private JSONObject createComment(UUID annotationId, String commentId) {
        JSONObject comment = new JSONObject();
        comment.put("id", commentId);
        comment.put("content", "text");
        comment.put("annotationId", annotationId);

        request
                .body(comment.toString())
                .post("/api/comments")
                .then()
                .statusCode(201)
                .body("id", equalTo(commentId))
                .body("content", equalTo("text"))
                .body("annotationId", equalTo(annotationId.toString()))
                .header("Location", equalTo("/api/comments/" + commentId))
                .log().all();
        return comment;
    }

    @NotNull
    private UUID createAnnotation(UUID newAnnotationSetId) {
        UUID annotationId = UUID.randomUUID();
        JSONObject createAnnotations = new JSONObject();
        createAnnotations.put("annotationSetId", newAnnotationSetId);
        createAnnotations.put("id", annotationId);
        createAnnotations.put("annotationType", "highlight");
        createAnnotations.put("page", 1);
        createAnnotations.put("color", "d1d1d1");

        request
                .body(createAnnotations)
                .post("/api/annotations")
                .then()
                .statusCode(201)
                .body("id", equalTo(annotationId.toString()));
        return annotationId;
    }

    @NotNull
    private UUID createAnnotationSet() {
        JSONObject jsonObject = new JSONObject();
        UUID newAnnotationSetId = UUID.randomUUID();
        jsonObject.put("documentId", UUID.randomUUID().toString());
        jsonObject.put("id", newAnnotationSetId.toString());

        request
                .body(jsonObject.toString())
                .post("/api/annotation-sets")
                .then()
                .statusCode(201);
        return newAnnotationSetId;
    }
}
