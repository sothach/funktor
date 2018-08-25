package utils

import javax.inject.{Inject, Singleton}
import org.apache.spark.{SparkConf, SparkContext}
import play.api.Configuration
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

@Singleton
class SparkConfiguration @Inject ()(configuration: Configuration, lifecycle: ApplicationLifecycle) {
  val driverHost = configuration.get[String]("spark.driver.host")
  val driverPort = configuration.get[Int]("spark.driver.port")

  val context = new SparkContext(new SparkConf(false) // skip loading external settings
    .setMaster("local[4]") // run locally with enough threads
    .setAppName("Scintilla")
    .set("spark.logConf", "false")
    .set("spark.driver.port", driverPort.toString)
    .set("spark.driver.host", driverHost))

  lifecycle.addStopHook {
    () => Future.successful(context.stop())
  }
}
