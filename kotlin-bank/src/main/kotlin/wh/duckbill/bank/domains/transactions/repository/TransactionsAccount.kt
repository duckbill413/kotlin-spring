package wh.duckbill.bank.domains.transactions.repository

import org.springframework.data.jpa.repository.JpaRepository
import wh.duckbill.bank.types.entity.Account
import wh.duckbill.bank.types.entity.User

interface TransactionsAccount : JpaRepository<Account, String> {
  fun findByUlidAndUser(ulid: String, user: User): Account?
  fun findByUlid(accountUlid: String): Account?
}