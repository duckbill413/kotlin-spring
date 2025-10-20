package wh.duckbill.bank.domains.transactions.service

import org.slf4j.Logger
import org.springframework.stereotype.Service
import wh.duckbill.bank.common.cache.RedisClient
import wh.duckbill.bank.common.cache.RedisKeyProvider
import wh.duckbill.bank.common.exception.CustomException
import wh.duckbill.bank.common.exception.ErrorCode
import wh.duckbill.bank.common.json.JsonUtil
import wh.duckbill.bank.common.logging.Logging
import wh.duckbill.bank.common.message.KafkaProducer
import wh.duckbill.bank.common.transaction.Transactional
import wh.duckbill.bank.domains.transactions.model.DepositResponse
import wh.duckbill.bank.domains.transactions.model.TransferResponse
import wh.duckbill.bank.domains.transactions.repository.TransactionsAccount
import wh.duckbill.bank.domains.transactions.repository.TransactionsUser
import wh.duckbill.bank.types.TransactionMessage
import wh.duckbill.bank.types.dto.Response
import wh.duckbill.bank.types.dto.ResponseProvider
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class TransactionService(
  private val transactionsUser: TransactionsUser,
  private val transactionsAccount: TransactionsAccount,
  private val kafkaProducer: KafkaProducer,
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

          val message = JsonUtil.encodeToJson(
            TransactionMessage(
              fromUlid = "0x0",
              fromName = "0x0",
              fromAccountId = "0x0",
              toUlid = userUlid,
              toName = user.username,
              toAccountId = accountUlid,
              value = value,
            ),
            TransactionMessage.serializer()
          )
          kafkaProducer.sendMessage("", message)

          ResponseProvider.success(DepositResponse(account.balance))
        }
      }
    }

  fun transfer(
    fromUlid: String,
    fromAccountId: String,
    toAccountId: String,
    value: BigDecimal
  ): Response<TransferResponse> =
    Logging.logFor(logger) { log ->
      log["fromUlid"] = fromUlid
      log["fromAccountId"] = fromAccountId
      log["toAccountId"] = toAccountId
      log["value"] = value

      val key = RedisKeyProvider.bankMutexKey(fromUlid, fromAccountId)
      redisClient.invokeWithMutex(key) {
        transactional.run {
          val fromUser = transactionsUser.findByUlid(fromUlid)
          val fromAccount = transactionsAccount.findByUlidAndUser(fromAccountId, fromUser)
            ?: throw CustomException(ErrorCode.FAILED_TO_FIND_ACCOUNT)

          val toAccount =
            transactionsAccount.findByUlid(toAccountId) ?: throw CustomException(ErrorCode.FAILED_TO_FIND_ACCOUNT)

          if (fromAccount.balance < value) {
            throw CustomException(ErrorCode.ENOUGH_VALUE)
          } else if (value <= BigDecimal.ZERO) {
            throw CustomException(ErrorCode.VALUE_MUST_NOT_BE_UNDER_ZERO)
          }

          fromAccount.balance = fromAccount.balance.subtract(value)
          toAccount.balance = toAccount.balance.add(value)
          fromAccount.updatedAt = LocalDateTime.now()
          transactionsAccount.save(fromAccount)
          transactionsAccount.save(toAccount)
          ResponseProvider.success(TransferResponse(fromAccount.balance, toAccount.balance))
        }
      }
    }
}