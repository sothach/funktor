package utils

import java.time.LocalDateTime
import java.util.UUID

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import model.Donation
import org.apache.spark.sql.SparkSession
import play.api.Logger

import scala.concurrent.Future

@Singleton
class Donations @Inject()(system: ActorSystem, db: Database) {
  val logger = Logger(this.getClass)
  implicit val ec = system.dispatcher

  def run = Future {
    logger.debug("running Donations")

    val spark = SparkSession
      .builder()
      .appName("Scintilla")
      .master("local")
      .getOrCreate()

    spark.read
      .format("jdbc")
      .option("url", db.url)
      .option("driver", db.driver)
      .option("dbtable", "donation_event")
      .load()
      .createOrReplaceTempView("donation_event")

    val folded = spark.sql(
      """SELECT a.id, a.aggregate_id, a.event_type, a.created, a.category, a.quantity, a.donor_id, a.recipient_id
         | FROM donation_event a
         |  INNER JOIN (
         |    SELECT aggregate_id, MAX(created) latest, id
         |    FROM donation_event WHERE event_type = 'Available'
         |    GROUP BY aggregate_id, id
         |  ) b ON a.id = b.id""".stripMargin)

    println("folded:")
    folded.sort("aggregate_id").show(100)

    val pivoted = folded
      .groupBy("aggregate_id")
      .pivot("category")
      .max("quantity")

    println("pivoted:")
    pivoted.sort("aggregate_id").show(100)

    val leftRight = folded
      .withColumnRenamed("category", "left")
      .join(folded.withColumnRenamed("category", "right"), Seq("id"))
    println("crosstab join:")
    leftRight.stat.crosstab("left", "right").show()

    val productTags = Array("BKR","CHL","FFV")

    val result = pivoted.collect() map { row =>
      lazy val products = (1 to 3) map { i =>
        if(!row.isNullAt(i)) {
          Some(productTags(i-1) -> row.getAs[Double](i))
        } else {
          None
        }
      }
      val ps = () => products.collect { case Some(t) => t}.toMap

      Donation(
        aggregateId=UUID.fromString(row.getString(0)),
        received=LocalDateTime.now,
        status="",
        donorId="",
        charityId=None,
        products=ps())
    }

    spark.close()
    result.toList
  }

}