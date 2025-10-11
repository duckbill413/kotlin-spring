package wh.duckbill.bank.domains.transactions.service

import org.slf4j.Logger
import org.springframework.stereotype.Service
import wh.duckbill.bank.common.cache.RedisClient
import wh.duckbill.bank.common.cache.RedisKeyProvider
import wh.duckbill.bank.common.exception.CustomException
import wh.duckbill.bank.common.exception.ErrorCode
import wh.duckbill.bank.common.logging.Logging
import wh.duckbill.bank.common.transaction.Transactional
import wh.duckbill.bank.domains.transactions.model.DepositResponse
import wh.duckbill.bank.domains.transactions.repository.TransactionsAccount
import wh.duckbill.bank.domains.transactions.repository.TransactionsUser
import wh.duckbill.bank.types.dto.Response
import wh.duckbill.bank.types.dto.ResponseProvider
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class TransactionService(
  private val transactionsUser: TransactionsUser,
  private val transactionsAccount: TransactionsAccount,
  private val redisClient: RedisClient,
  private val transactional: Transactional,
  private val logger: Logger = Logging.getLogger(TransactionService::class.java)
) {

  fun deposit(userUlid: String, accountUlid: String, value: BigDecimal): Response<DepositResponse> =
    Logging.logFor(logger) { log ->
      log["userUlid"] = userUlid
      log["accountUlid"] = accountUlid
      log["value"] = value

      val key = RedisKeyProvider.bankMutexKey(userUlid, accountUlid)
      redisClient.invokeWithMutex(key) {
        transactional.run {
          val user = transactionsUser.findByUlid(userUlid)
          val account = transactionsAccount.findByUlidAndUser(accountUlid, user)
            ?: throw CustomException(ErrorCode.FAILED_TO_FIND_ACCOUNT)

          account.balance = account.balance.add(value)
          account.updatedAt = LocalDateTime.now()
          transactionsAccount.save(account)
          ResponseProvider.success(DepositResponse(account.balance))
        }
      }
    }
}