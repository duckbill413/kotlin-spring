package wh.duckbill.bank.domains.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import wh.duckbill.bank.types.entity.User

interface AuthUserRepository : JpaRepository<User, String> {
  fun existsByUsername(username: String): Boolean

  @Modifying
  @Query("UPDATE User u SET u.accessToken = :accessToken WHERE u.username = :username")
  fun updateAccessTokenByUsername(
    @Param("username") username: String,
    @Param("accessToken") accessToken: String
  )
}