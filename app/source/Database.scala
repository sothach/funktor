package source

import javax.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class Database @Inject()(configuration: Configuration) {
  val driver = configuration.get[String]("db.scidb.driver")
  val url = configuration.get[String]("db.scidb.url")
}
