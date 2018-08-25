package utils

import java.util.UUID

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import model.Donation
import org.apache.spark.sql.SparkSession
import play.api.{Configuration, Logger}

import scala.concurrent.Future

@Singleton
class DonationsX @Inject()(system: ActorSystem, configuration: Configuration) {
  val logger = Logger(this.getClass)
  implicit val ec = system.dispatcher

  def run = Future {
    logger.debug("running Donations")

    val spark = SparkSession
      .builder()
      .appName("Scintilla")
      .master("local")
      .getOrCreate()

    val driver = configuration.get[String]("db.scidb.driver")
    val dbUrl = configuration.get[String]("db.scidb.url")

    val df = spark.read
      .format("jdbc")
      .option("url", dbUrl)
      .option("driver", driver)
      .option("dbtable", "donation_event")
      .load()
    df.createOrReplaceTempView("donation_event")

    val query = df
      .select("timestamp","event_type","aggregate_id")
      .filter(row => row.getAs[String]("event_type") == "Available")

    query.show()

    val sqlDF = spark.sql("SELECT * FROM donation_event WHERE event_type == 'Available'")
    sqlDF.printSchema()
    val result = sqlDF
      .limit(15)
      .collect() map { row =>
        Donation(
          UUID.fromString(row.getString(4)),
          row.getTimestamp(1).toLocalDateTime,
          row.getString(2),
          row.getString(5),
          Option(row.getString(6)), Map())
    }

    val folded = spark.sql(
      """SELECT a.id, a.aggregate_id, a.event_type, a.created, a.category, a.quantity, a.donor_id, a.recipient_id
         | FROM donation_event a
         |  INNER JOIN (
         |    SELECT aggregate_id, MAX(created) latest, id
         |    FROM donation_event WHERE event_type == 'Available'
         |    GROUP BY aggregate_id, id
         |  ) b ON a.id = b.id""".stripMargin)

    val q2 = folded
      .groupBy("aggregate_id")
      .pivot("category")
      .max("quantity")


    println("q2 folded:")
    q2.sort("aggregate_id").show(100)

    val leftRight = folded
      .withColumnRenamed("category", "left")
      .join(folded.withColumnRenamed("category", "right"), Seq("id"))
    println("crosstab join:")
    leftRight.stat.crosstab("left", "right").show()

    println("q3 join:")
    val q3 = q2
      .join(folded, q2.col("aggregate_id") === folded.col("aggregate_id") &&
        (q2.col("BKR") === folded.col("category") || q2.col("CHL") === folded.col("category") || q2.col("FFV") === folded.col("category")))
      .orderBy("created")

    q3.printSchema()

    val productTags = Array("BKR","CHL","FFV")

    val q4 = q3.collect() map { row =>

      lazy val products = (1 to 3) map { i =>
        if(!row.isNullAt(i)) {
          Some(productTags(i-1) -> row.getAs[Double](i))
        } else {
          None
        }
      }
      val ps = () => products.collect { case Some(t) => t}.toMap

      Donation(
        aggregateId=UUID.fromString(row.getString(5)),
        received=row.getTimestamp(7).toLocalDateTime,
        status=row.getString(6),
        donorId=row.getString(10),
        charityId=Option(row.getString(11)),
        products=ps())
    }


    q3.show(50)

    q4.toList
  }

}