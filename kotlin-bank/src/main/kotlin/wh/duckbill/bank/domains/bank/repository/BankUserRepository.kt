package wh.duckbill.bank.domains.bank.repository

import org.springframework.data.jpa.repository.JpaRepository
import wh.duckbill.bank.types.entity.User

interface BankUserRepository : JpaRepository<User, String> {
}