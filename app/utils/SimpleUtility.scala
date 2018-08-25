package utils

import javax.inject.{Inject, Singleton}
import play.api.Logger

@Singleton
class SimpleUtility @Inject()(sparkConfiguration: SparkConfiguration) {
  val logger = Logger(this.getClass)

  def simpleApp {
    logger.debug("running simpleApp")
    val logFile = "public/data/README.md" // Should be some file on your system
    val logData = sparkConfiguration.context.textFile(logFile, 4).cache()
    val numSparks = logData.filter(line => line.contains("Spark")).count()
  }

}