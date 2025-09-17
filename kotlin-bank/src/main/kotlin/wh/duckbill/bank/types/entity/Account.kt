package wh.duckbill.bank.types.entity

import jakarta.persistence.Basic
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "account")
data class Account (
  @Id
  @Column(name = "ulid", length = 12, nullable = false)
  val ulid: String,

  @Column(name = "balance", nullable = false, precision = 15, scale = 2)
  var balance: BigDecimal = BigDecimal.ZERO,

  @Column(name = "account_number", nullable = false, length = 100, unique = true)
  val accountNumber: String,

  @Column(name = "is_deleted", nullable = false)
  val isDeleted: Boolean = false,

  @Column(name = "created_at", nullable = false, updatable = false)
  val createdAt: LocalDateTime = LocalDateTime.now(),

  @Column(name = "updated_at", nullable = false)
  val updatedAt: LocalDateTime = LocalDateTime.now(),

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_ulid", nullable = false)
  val user: User
)