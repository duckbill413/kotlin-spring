package wh.duckbill.bank.domains.bank.service

import com.github.f4b6a3.ulid.UlidCreator
import org.slf4j.Logger
import org.springframework.stereotype.Service
import wh.duckbill.bank.common.exception.CustomException
import wh.duckbill.bank.common.exception.ErrorCode
import wh.duckbill.bank.common.logging.Logging
import wh.duckbill.bank.common.transaction.Transactional
import wh.duckbill.bank.domains.bank.repository.BankAccountRepository
import wh.duckbill.bank.domains.bank.repository.BankUserRepository
import wh.duckbill.bank.types.dto.Response
import wh.duckbill.bank.types.dto.ResponseProvider
import wh.duckbill.bank.types.entity.Account
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class BankService(
  private val transactional: Transactional,
  private val logger: Logger = Logging.getLogger(BankService::class.java),
  private val bankUserRepository: BankUserRepository,
  private val bankAccountRepository: BankAccountRepository
) {
  fun createAccount(userUlid: String): Response<String> = Logging.logFor(logger) { log ->
    log["userUlid"] = userUlid
    transactional.run {
      val user = bankUserRepository.findByUlid(userUlid)
      val ulid = UlidCreator.getUlid().toString()
      val accountNumber = generateRandomAccountNumber()

      val account: Account = Account(
        ulid = ulid,
        user = user,
        accountNumber = accountNumber,
      )

      try {
        bankAccountRepository.save(account)
      } catch (e: Exception) {
        throw CustomException(ErrorCode.FAILED_TO_SAVE_DATA, e.message)
      }
    }

    return@logFor ResponseProvider.success("SUCCESS")
  }

  fun balance(userUlid: String, accountUlid: String): Response<BigDecimal> = Logging.logFor(logger) { log ->
    log["userUlid"] = userUlid
    log["accountUlid"] = accountUlid

    // TODO -> 동시성 처리 고려

    return@logFor transactional.run {
      val account =
        bankAccountRepository.findByUlid(accountUlid) ?: throw CustomException(ErrorCode.FAILED_TO_FIND_ACCOUNT)
      if (account.user.ulid != userUlid) {
        throw CustomException(ErrorCode.MISS_MATCH_ACCOUNT_ULID_AND_USER_ULID)
      }

      ResponseProvider.success(account.balance)
    }
  }

  fun removeAccount(userUlid: String, accountUlid: String): Response<String> = Logging.logFor(logger) { log ->
    log["userUlid"] = userUlid
    log["accountUlid"] = accountUlid

    transactional.run {
      val user = bankUserRepository.findByUlid(userUlid)
      val account =
        bankAccountRepository.findByUlid(accountUlid) ?: throw CustomException(ErrorCode.FAILED_TO_FIND_ACCOUNT)
      if (account.user.ulid != user.ulid) {
        throw CustomException(ErrorCode.MISS_MATCH_ACCOUNT_ULID_AND_USER_ULID)
      }
      if (account.balance.compareTo(BigDecimal.ZERO) != 0) {
        throw CustomException(ErrorCode.ACCOUNT_BALANCE_IS_NOT_ZERO, account.balance.toString())
      }

      val updatedAccount = account.copy(
        isDeleted = true,
        updatedAt = LocalDateTime.now(),
      )

      bankAccountRepository.save(updatedAccount)
      ResponseProvider.success("SUCCESS")
    }
    return@logFor ResponseProvider.success("SUCCESS")
  }

  private fun generateRandomAccountNumber(): String {
    val bankCode = "003"
    val section = "12"

    val number = Math.random().toString()
    return "$bankCode-$section-$number"
  }
}