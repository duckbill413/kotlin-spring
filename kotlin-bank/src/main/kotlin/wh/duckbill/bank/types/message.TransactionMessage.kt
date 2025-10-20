package wh.duckbill.bank.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import wh.duckbill.bank.common.json.BigDecimalSerializer
import wh.duckbill.bank.common.json.LocalDateTimeSerializer
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
)