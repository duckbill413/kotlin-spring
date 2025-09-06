package org.coroutine.동시성2.BASE_1.`35`

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

data class Account(
  val id: Int,
  var balance: Int,
  val mutex: Mutex = Mutex() // 동시성 처리를 위한 객체

)

suspend fun transfer(from: Account, to: Account, amount: Int) {
  // 데드락을 방지하기 위해서 항상 id 순서로 lock 획득하고 반환하는 작업
  val (first, second) = if (from.id < to.id) from to to else to to from

  first.mutex.withLock {
    second.mutex.withLock {
      if (from.balance >= amount) {
        from.balance -= amount
        to.balance += amount
        println("Transfer ${from.id} -> ${to.id}: $amount success")
      }
    }
  }
}

fun main(): Unit = runBlocking {
  val accounts = List(3) { Account(it, 1000) }

  var count = 0
  val jobs = List(100) {
    launch {
      repeat(1200) {
        val fromIdx = Random.nextInt(accounts.size)
        var toIdx = Random.nextInt(accounts.size)
        while (toIdx == fromIdx) {
          toIdx = Random.nextInt(accounts.size)
        }
        val amount = Random.nextInt(1, 30)
        transfer(accounts[fromIdx], accounts[toIdx], amount)
        count++
      }
    }
  }

  jobs.forEach { it.join() }
  val total = accounts.sumOf { it.balance }
  println("Total balance: $total count: $count")
}