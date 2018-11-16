package uk.gov.hmcts.reform.em.annotation.performance
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.em.annotation.testutil.Env
class AnnoSmoke extends Simulation with HttpConfiguration {
  object Health {
    val health = exec(http("health")
      .get(Env.getTestUrl() + "/health"))
  }

  val scn = scenario("Anno smoke test").exec(Health.health)
  setUp(scn.inject(atOnceUsers(1)).protocols(httpProtocol))
}
