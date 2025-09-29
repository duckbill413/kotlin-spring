package wh.duckbill.bank.domains.bank.service

import org.slf4j.Logger
import org.springframework.stereotype.Service
import wh.duckbill.bank.common.logging.Logging
import wh.duckbill.bank.common.transaction.Transactional
import wh.duckbill.bank.domains.bank.repository.BankAccountRepository
import wh.duckbill.bank.domains.bank.repository.BankUserRepository
import wh.duckbill.bank.types.dto.Response
import java.math.BigDecimal

@Service
class BankService(
  private val transactional: Transactional,
  private val logger: Logger = Logging.getLogger(BankService::class.java),
  private val bankUserRepository: BankUserRepository,
  private val bankAccountRepository: BankAccountRepository
) {
  fun createAccount(userUlid: String): Response<String> = Logging.logFor(logger) { log ->
    log["userUlid"] = userUlid
    transactional.run {}
  }

  fun balance(userUlid: String, accountUlid: String): Response<BigDecimal> = Logging.logFor(logger) { log ->
    log["userUlid"] = userUlid
    log["accountUlid"] = accountUlid
    transactional.run {}
  }

  fun removeAccount(userUlid: String, accountUlid: String): Response<String> = Logging.logFor(logger) { log ->
    log["userUlid"] = userUlid
    log["accountUlid"] = accountUlid
    transactional.run {}
  }
}