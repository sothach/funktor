package utils

import javax.inject.{Inject, Singleton}
import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}
import play.api.Logger

@Singleton
class TwitterPopularTags @Inject ()(
      sparkConfiguration: SparkConfiguration, twitterConfig: TwitterConfig) {
  val logger = Logger(this.getClass)

  def twitterStreamUtil {
    logger.debug("running twitterStreamUtil")

    val ssc = new StreamingContext(sparkConfiguration.context, Seconds(10))
    val filters = Seq("trump")
    val stream = TwitterUtils.createStream(ssc, Option(twitterConfig.authorization), filters)

    val hashTags = stream.flatMap(status => status.getText.split(" ").filter(_.startsWith("#")))
    
    val topCounts60 = hashTags.map((_, 1)).reduceByKeyAndWindow(_ + _, Minutes(60))
      .map { case (topic, count) => (count, topic) }
      .transform(_.sortByKey(ascending = false))
          
    val topCounts10 = hashTags.map((_, 1)).reduceByKeyAndWindow(_ + _, Minutes(10))
      .map { case (topic, count) => (count, topic) }
      .transform(_.sortByKey(ascending = false))

    topCounts60.foreachRDD(rdd => {
      val topList = rdd.take(5)
      logger.info("Popular topics in last 60 seconds (%s total):".format(rdd.count()))
      topList.foreach { case (count, tag) => logger.info(s"$tag ($count tweets)") }
    })

    topCounts10.foreachRDD(rdd => {
      val topList = rdd.take(5)
      logger.info("Popular topics in last 10 seconds (%s total):".format(rdd.count()))
      topList.foreach { case (count, tag) => logger.info(s"$tag ($count tweets)") }
    })

    ssc.start()
    ssc.awaitTermination()
  }

}
