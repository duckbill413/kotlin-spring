package wh.duckbill.bank.domains.transactions.repository

import org.springframework.data.jpa.repository.JpaRepository
import wh.duckbill.bank.types.entity.User

interface TransactionsUser : JpaRepository<User, String> {
  fun findByUlid(uuid: String): User
}