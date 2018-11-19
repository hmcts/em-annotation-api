package uk.gov.hmcts.reform.em.annotation.performance
import java.util.UUID

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.json.{JSONArray, JSONObject}
import org.springframework.http.MediaType
import uk.gov.hmcts.reform.em.annotation.testutil.{Env, TestUtil}

class AnnoLoad extends Simulation with HttpConfiguration {
  val testUtil = new TestUtil

  object CreateAnnotationSet {
    val authorization = testUtil.generateIdamToken("test@test.com")
    val serviceAuthorization =  testUtil.getS2sToken()

    val jsonObject = new JSONObject
    val newAnnotationSetId: UUID = UUID.randomUUID
    jsonObject.put("documentId", UUID.randomUUID.toString)
    jsonObject.put("id", newAnnotationSetId.toString)

    val createTask = exec(http("createTask")
      .post(Env.getTestUrl + "/api/annotation-sets")
      .header("Authorization", authorization)
      .header("ServiceAuthorization", serviceAuthorization)
      .body(StringBody(jsonObject.toString))
      .check(
        status.find.in(201),
      )
    )
  }

  object CreateAnnotations {

    val annotationJsonObject = new JSONObject
    val annotationId: UUID = UUID.randomUUID
    annotationJsonObject.put("annotationSetId", CreateAnnotationSet.newAnnotationSetId)
    annotationJsonObject.put("id", annotationId.toString)
    annotationJsonObject.put("page", "1")
    annotationJsonObject.put("color", "FFFF00") // this can be constant
    annotationJsonObject.put("type", "highlight") // this can be constant

    val commentsJsonArray = new JSONArray
    val commentJsonObject = new JSONObject
    val commentId: UUID = UUID.randomUUID
    commentJsonObject.put("id", commentId.toString)
    commentJsonObject.put("content", "Added comment for stress test")
    commentJsonObject.put("annotationId", annotationId.toString)
    commentsJsonArray.put(0, commentJsonObject)
    annotationJsonObject.put("comment", commentsJsonArray)

    val rectangles = new JSONArray
    val rectangle = new JSONObject
    rectangle.put("id", UUID.randomUUID.toString)
    rectangle.put("annotationId", annotationId.toString)
    rectangle.put("x", 0)
    rectangle.put("y", 0)
    rectangle.put("width", 10)
    rectangle.put("height", 10)
    rectangles.put(0, rectangle)
    annotationJsonObject.put("rectangles", rectangles)

    val createAnnotation = exec(http("createAnnotation")
      .post(Env.getTestUrl + "/api/annotations")
      .header("Authorization", CreateAnnotationSet.authorization)
      .header("ServiceAuthorization", CreateAnnotationSet.serviceAuthorization)
      .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
      .body(StringBody(annotationJsonObject.toString))
      .check(
        status.find.in(201)
      ))
  }

//  val scn = scenario("Anno load test").exec(CreateAnnotationSet.createTask)
  val scn = scenario("Anno creation test")
                .exec(CreateAnnotationSet.createTask)
                .exec(CreateAnnotations.createAnnotation)

  setUp(scn.inject(atOnceUsers(1)).protocols(httpProtocol))
    .assertions(details("createTask").failedRequests.percent.is(0))

//  setUp(scn2.inject(atOnceUsers(1)).protocols(httpProtocol))
//    .assertions(details("createAnnotations").failedRequests.percent.is(0))
}