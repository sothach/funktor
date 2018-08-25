package utils

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken

@Singleton
class TwitterConfig @Inject()(configuration: Configuration) {

  val consumerKey = configuration.get[String]("twitter.consumer.key")
  val consumerSecret = configuration.get[String]("twitter.consumer.secret")
  val accessToken = configuration.get[String]("twitter.access.token")
  val accessTokenSecret = configuration.get[String]("twitter.access.secret")

  val authorization = {
    val result = new TwitterFactory().getInstance()
    result.setOAuthConsumer(consumerKey, consumerSecret)
    result.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret))
    result.getAuthorization
  }

}
