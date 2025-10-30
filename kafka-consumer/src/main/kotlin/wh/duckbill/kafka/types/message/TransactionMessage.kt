package wh.duckbill.kafka.types.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import wh.duckbill.kafka.common.json.BigDecimalSerializer
import wh.duckbill.kafka.common.json.LocalDateTimeSerializer
import wh.duckbill.kafka.types.entity.TransactionHistoryDocument
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class TransactionMessage(
  @SerialName("fromUlid")
  val fromUlid: String,

  @SerialName("fromName")
  val fromName: String,

  @SerialName("fromAccountId")
  val fromAccountId: String,

  @SerialName("toUlid")
  val toUlid: String,

  @SerialName("toName")
  val toName: String,

  @SerialName("toAccountId")
  val toAccountId: String,

  @SerialName("value")
  @Serializable(with = BigDecimalSerializer::class)
  val value: BigDecimal,

  @SerialName("time")
  @Serializable(with = LocalDateTimeSerializer::class)
  var time: LocalDateTime = LocalDateTime.now(),
) {
  fun toEntity() : TransactionHistoryDocument = TransactionHistoryDocument(
    fromUlid = fromUlid,
    toUlid = toUlid,
    value = value,
    time = time,
  )
}