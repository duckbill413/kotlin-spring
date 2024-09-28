package wh.duckbill.userservice.domain.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import wh.duckbill.userservice.domain.entity.User

interface UserRepository : CoroutineCrudRepository<User, Long> {
    suspend fun findByEmail(email: String): User?
}