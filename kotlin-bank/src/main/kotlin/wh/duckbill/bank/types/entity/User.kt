package wh.duckbill.bank.types.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.engine.internal.Cascade.cascade
import java.time.LocalDateTime

@Entity
@Table(name = "user")
data class User (
  @Id
  @Column(name = "ulid", length = 12, nullable = false)
  val ulid: String,

  @Column(name = "platform", nullable = false, length = 25)
  val platform: String,

  @Column(name = "username", nullable = false, unique = true, length = 50)
  val username: String,

  @Column(name = "access_token", length = 255)
  val accessToken: String? = null,

  @Column(name = "created_at", nullable = false, updatable = false)
  val createdAt: LocalDateTime = LocalDateTime.now(),

  @OneToMany(mappedBy = "user")
  val accounts: List<Account> = emptyList()
)