import akka.util.Timeout
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._
import play.api.db.evolutions._
import play.api.db.Databases
import scala.concurrent.duration._

class ApplicationSpec extends PlaySpec with ScalaFutures with GuiceOneAppPerSuite with BeforeAndAfterAll with DefaultAwaitTimeout {

  override implicit val defaultAwaitTimeout: Timeout = 120 seconds

  val database = Databases.inMemory(name = "scidb",
    urlOptions = Map(
      "MODE" -> "PostgreSQL",
      "DATABASE_TO_UPPER" -> "false",
      "TRACE_LEVEL_SYSTEM_OUT" -> "1",
      "DB_CLOSE_DELAY" -> "-1",
      "DB_CLOSE_ON_EXIT" -> "false"),
      config = Map("logStatements" -> true))

  val config = ConfigFactory.load("test-application.conf")
    .withValue("db.scidb.url", ConfigValueFactory.fromAnyRef(database.url))
    .withValue("db.scidb.driver", ConfigValueFactory.fromAnyRef("org.h2.Driver"))

  override def fakeApplication() = new GuiceApplicationBuilder()
    .loadConfig(Configuration(config))
    .configure(Map("play.filters.disabled" -> Seq("play.filters.csrf.CSRFFilter"),
      "play.filters.hosts.allowed" -> Seq("localhost")))
    .build()

  "Calling the /donations endpoint" should {
    "be routed successfully" in {
      val request = FakeRequest("GET", s"/donations").withHeaders(fakedHeaders)

      route(app, request).map(status) mustBe Some(OK)
    }
  }

  val fakedHeaders = FakeHeaders(Map("Host" -> "localhost").toSeq)

  override def beforeAll = {
    Evolutions.applyEvolutions(database)
  }

  override def afterAll = {
    Evolutions.cleanupEvolutions(database)
  }

}
