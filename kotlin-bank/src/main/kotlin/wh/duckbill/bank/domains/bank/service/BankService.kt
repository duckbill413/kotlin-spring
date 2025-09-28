package wh.duckbill.bank.domains.bank.service

import org.slf4j.Logger
import org.springframework.stereotype.Service
import wh.duckbill.bank.common.logging.Logging
import wh.duckbill.bank.common.transaction.Transactional

@Service
class BankService(
  private val transactional: Transactional,
  private val logger: Logger = Logging.getLogger(BankService::class.java)
) {
  fun createAccount(userUlid: String) = Logging.logFor(logger) { log ->
    log["userUlid"] = userUlid
    transactional.run {}
  }

  fun balance(userUlid: String, accountUlid: String) = Logging.logFor(logger) { log ->
    log["userUlid"] = userUlid
    log["accountUlid"] = accountUlid
    transactional.run {}
  }

  fun removeAccount(userUlid: String, accountUlid: String) = Logging.logFor(logger) { log ->
    log["userUlid"] = userUlid
    log["accountUlid"] = accountUlid
    transactional.run {}
  }
}