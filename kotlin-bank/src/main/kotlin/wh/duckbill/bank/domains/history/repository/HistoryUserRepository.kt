package wh.duckbill.bank.domains.history.repository

import org.springframework.data.jpa.repository.JpaRepository
import wh.duckbill.bank.types.entity.User

interface HistoryUserRepository : JpaRepository<User, String> {
  fun findByUlid(ulid: String): User
}