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
  val fromUser: String,

  val toUlid: String,
  val toUser: String,

  @Serializable(with = BigDecimalSerializer::class)
  val value: BigDecimal,

  @Serializable(with = LocalDateTimeSerializer::class)
  val time: LocalDateTime,
)

fun TransactionHistoryDocument.toHistory(fromUser: String, toUser: String): History = History(
  fromUlid = fromUlid,
  fromUser = fromUser,
  toUlid = toUlid,
  toUser = toUser,
  value = value,
  time = time,
)

fun TransactionMessage.toHistory(fromUser: String, toUser: String): History = History(
  fromUlid = fromUlid,
  fromUser = fromUser,
  toUlid = toUlid,
  toUser = toUser,
  value = value,
  time = time,
)