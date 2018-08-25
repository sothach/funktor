package model

import java.time.LocalDateTime
import java.util.UUID

case class Donation(aggregateId: UUID, received: LocalDateTime, status: String,
                    donorId: String, charityId: Option[String], products: Map[String,Double])
