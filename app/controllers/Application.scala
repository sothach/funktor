package controllers

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.mvc._

import scala.concurrent.Future
import utils._

@Singleton
class Application @Inject()(system: ActorSystem,
                            simpleUtility: SimpleUtility,
                            twitterTags: TwitterPopularTags,
                            sparkMLLibUtility: SparkMLLibUtility,
                            sparkSQL: SparkSQL,
                            donationSource: Donations,
                            components: ControllerComponents) extends AbstractController(components) {

  implicit val ec = system.dispatcher

  def mlib = Action {
    Future{sparkMLLibUtility.sparkMLLibExample}
    Ok(views.html.main("")(List(),List()))
  }

  def simple = Action {
    Future{simpleUtility.simpleApp}
    Ok(views.html.main("")(List(),List()))
  }

  def twitter = Action {
    Future{twitterTags.twitterStreamUtil}
    Ok(views.html.main("")(List(),List()))
  }

  def sql = Action {
    Future{sparkSQL.simpleSparkSQLApp}
    Ok(views.html.main("")(List(),List()))
  }

  def donations = Action.async {
    donationSource.run map { results =>
      Ok(views.html.main("")(List("Received","ID","Status","Donor","Charity","BKR","CHL","FFV"),results))
    }
  }


}
