package wh.duckbill.bank.domains.transactions.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import wh.duckbill.bank.domains.transactions.model.DepositRequest
import wh.duckbill.bank.domains.transactions.model.DepositResponse
import wh.duckbill.bank.domains.transactions.model.TransferRequest
import wh.duckbill.bank.domains.transactions.model.TransferResponse
import wh.duckbill.bank.domains.transactions.service.TransactionService
import wh.duckbill.bank.types.dto.Response

@RestController
@RequestMapping("/api/v1/transaction")
class TransactionController(
  private val transactionService: TransactionService
) {
  @PostMapping("/deposit")
  fun deposit(@RequestBody depositRequest: DepositRequest): Response<DepositResponse> {
    return transactionService.deposit(
      depositRequest.toUlid,
      depositRequest.toAccountId,
      depositRequest.value,
    )
  }

  @PostMapping("/transfer")
  fun transfer(@RequestBody transferRequest: TransferRequest): Response<TransferResponse> {
    return transactionService.transfer(
      transferRequest.fromUlid,
      transferRequest.fromAccountId,
      transferRequest.toAccountId,
      transferRequest.value,
    )
  }
}