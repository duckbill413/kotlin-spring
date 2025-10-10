package wh.duckbill.bank.domains.transactions.model

import java.math.BigDecimal

data class DepositResponse(
  val afterBalance: BigDecimal,
)

data class TransferResponse(
  val afterFromBalance: BigDecimal,
  val afterToBalance: BigDecimal,
)