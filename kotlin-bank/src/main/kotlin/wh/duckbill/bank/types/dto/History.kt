package wh.duckbill.bank.types.dto

import kotlinx.serialization.Serializable
import wh.duckbill.bank.common.json.BigDecimalSerializer
import wh.duckbill.bank.common.json.LocalDateTimeSerializer
import wh.duckbill.bank.types.entity.TransactionHistoryDocument
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

fun TransactionHistoryDocument.toHistory(): History = History(
  fromUlid = fromUlid,
  fromUser = fromUlid,
  toUlid = toUlid,
  toUser = toUlid,
  value = value,
  time = time,
)