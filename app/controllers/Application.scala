package controllers

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.mvc._

import donations._

@Singleton
class Application @Inject()(system: ActorSystem,
                            donationSource: Donations,
                            components: ControllerComponents) extends AbstractController(components) {

  implicit val ec = system.dispatcher

  def donations = Action.async {
    donationSource.run map { results =>
      Ok(views.html.main("")(List("Received","ID","Status","Donor","Charity","BKR","CHL","FFV"),results))
    }
  }


}
