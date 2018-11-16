package uk.gov.hmcts.reform.em.annotation.performance
import java.util.UUID

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.json.JSONObject
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
    println(jsonObject.get("id"))
    println(jsonObject.get("documentId"))

    val createTask = exec(http("createTask")
      .post(Env.getTestUrl + "/api/annotation-sets")
      .header("Authorization", authorization)
      .header("ServiceAuthorization", serviceAuthorization)
      .body(StringBody(jsonObject.toString))
      .check(
        status.find.in(201),
        jsonPath("$.createdBy").in(testUtil.getUserId.toString
      )
    ))
  }

  val scn = scenario("Anno load test").exec(CreateAnnotationSet.createTask)
  setUp(scn.inject(atOnceUsers(1)).protocols(httpProtocol))
    .assertions(details("createTask").failedRequests.percent.is(0))
}