package wh.duckbill.kafka.types.entity

import kotlinx.serialization.Serializable
import org.springframework.data.mongodb.core.mapping.Document
import wh.duckbill.kafka.common.json.BigDecimalSerializer
import wh.duckbill.kafka.common.json.LocalDateTimeSerializer
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
@Document(collection = "transaction_history")
data class TransactionHistoryDocument(

  val fromUlid: String,

  val toUlid: String,

  @Serializable(with = BigDecimalSerializer::class)
  val value: BigDecimal,

  @Serializable(with = LocalDateTimeSerializer::class)
  val time: LocalDateTime
)