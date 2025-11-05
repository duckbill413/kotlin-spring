package wh.duckbill.kafka.types.dto

import kotlinx.serialization.Serializable
import wh.duckbill.kafka.common.json.BigDecimalSerializer
import wh.duckbill.kafka.common.json.LocalDateTimeSerializer
import wh.duckbill.kafka.types.entity.TransactionHistoryDocument
import wh.duckbill.kafka.types.message.TransactionMessage
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class History(
  val fromUlid: String,
  val fromName: String,

  val toUlid: String,
  val toName: String,

  @Serializable(with = BigDecimalSerializer::class)
  val value: BigDecimal,

  @Serializable(with = LocalDateTimeSerializer::class)
  val time: LocalDateTime,
)

fun TransactionHistoryDocument.toHistory(): History = History(
  fromUlid = fromUlid,
  fromName = fromUlid,
  toUlid = toUlid,
  toName = toUlid,
  value = value,
  time = time,
)

fun TransactionMessage.toHistory(): History = History(
  fromUlid = fromUlid,
  fromName = fromName,
  toUlid = toUlid,
  toName = toName,
  value = value,
  time = time,
)