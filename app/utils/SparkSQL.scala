package utils

import org.apache.spark.sql.SparkSession
import play.api.Logger

case class WordCount(word: String, count: Int)

import javax.inject.{Inject, Singleton}

@Singleton
class SparkSQL @Inject()(sparkConfiguration: SparkConfiguration) {
  val logger = Logger(this.getClass)

  def simpleSparkSQLApp {
    logger.debug("running simpleSparkSQLApp")

    val logFile = "public/data/example.txt" // Should be some file on your system

    //val sqlContext = new SQLContext(sc)
    val sqlContext = SparkSession
      .builder()
      .appName("Scintilla")
      .master("local")
      .getOrCreate()

    val logData = sparkConfiguration.context.textFile(logFile, 4).cache()
    val words = logData.flatMap(_.split("\\s+"))

    import sqlContext.implicits._
    
    val wordCount = words.map(word => (word,1)).reduceByKey(_+_).map(wc => WordCount(wc._1, wc._2))
    val wcDf = wordCount.toDF()
    wcDf.createOrReplaceTempView("wordCount")

    val moreThanTenCounters = wcDf.where('count > 10).select('word)
    
    println("Words occurring more than 10 times are : ")
    moreThanTenCounters.map(mttc => "Word : " + mttc(0)).collect().foreach(println)
    
  }

}