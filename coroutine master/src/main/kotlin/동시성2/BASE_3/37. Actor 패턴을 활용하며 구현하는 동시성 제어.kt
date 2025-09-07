package org.coroutine.동시성2.BASE_3

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor


// 계좌 관련 메시지들의 기본 클래스
sealed class AccountMsg

// 입금을 위한 메시지 클래스 - 입금액을 포함
data class Deposit(val amount: Int) : AccountMsg()

// 출금을 위한 메시지 클래스 - 출금액과 성공/실패 응답을 위한 CompletableDeferred 포함
data class Withdraw(val amount: Int, val response: CompletableDeferred<Boolean>) : AccountMsg()

// 잔액 조회를 위한 메시지 클래스 - 잔액 응답을 위한 CompletableDeferred 포함
// 잔액 조회를 위한 메시지 클래스
data class GetBalance(val response: CompletableDeferred<Int>) : AccountMsg()

// 계좌 이체를 위한 메시지 클래스 - 이체금액, 대상 계좌, 성공/실패 응답을 포함
data class Transfer(
  val amount: Int,
  val to: SendChannel<AccountMsg>,
  val response: CompletableDeferred<Boolean>
) : AccountMsg()

// 계좌 Actor를 생성하는 확장 함수
// Actor 패턴을 사용하여 동시성 제어를 구현
// Actor 패턴: 각 개체(Actor)는 자신만의 독립적인 상태를 가지며, 다른 Actor와 메시지를 주고받음으로써 상호작용
// CompletableDeferred: 비동기 작업의 결과를 나중에 완료할 수 있는 Deferred 객체로,
// Future/Promise 패턴을 구현한 코틀린의 비동기 결과 처리 방식
fun CoroutineScope.accountActor(initialBalance: Int) = actor<AccountMsg> {
  // 계좌 잔액 초기화
  var balance = initialBalance
  // 채널로부터 메시지를 수신하여 처리
  for (msg in channel) {
    when (msg) {
      // 입금 처리
      is Deposit -> balance += msg.amount
      // 출금 처리 - 잔액이 충분한 경우에만 출금 가능
      is Withdraw -> {
        if (balance >= msg.amount) {
          balance -= msg.amount
          msg.response.complete(true)
        } else {
          msg.response.complete(false)
        }
      }
      // 잔액 조회 처리
      is GetBalance -> msg.response.complete(balance)
      // 계좌 이체 처리 - 잔액이 충분한 경우에만 이체 가능
      is Transfer -> {
        if (balance >= msg.amount) {
          balance -= msg.amount
          msg.to.send(Deposit(msg.amount))
          msg.response.complete(true)
        } else {
          msg.response.complete(false)
        }
      }
    }
  }
}

fun main(): Unit = runBlocking {
  // 초기 잔액이 1000인 A 계좌와 500인 B 계좌 생성
  val A = accountActor(1000)
  val B = accountActor(500)

  // 비동기 작업들을 저장할 리스트
  val jobs = mutableListOf<Job>()

  // A 계좌에 200 입금
  jobs += launch {
    A.send(Deposit(200))
  }

  // A 계좌에서 300 출금
  jobs += launch {
    val result = CompletableDeferred<Boolean>()
    A.send(Withdraw(300, result))
    println("A 출금(300) ${result.await()}")
  }

  // A 계좌에서 B 계좌로 400 이체
  jobs += launch {
    val result = CompletableDeferred<Boolean>()
    A.send(Transfer(400, B, result))
    println("A -> B 이체 (400) ${result.await()}")
  }

  // 100번 반복하여 잔액 조회와 출금 시도
  repeat(100) {
    // 잔액 조회
    jobs += launch {
      val result = CompletableDeferred<Int>()
      A.send(GetBalance(result))
      println("A.send(GetBalance(result)) = ${result.await()}")
    }
    // 10원 출금 시도
    jobs += launch {
      val result = CompletableDeferred<Boolean>()
      A.send(Withdraw(10, result))
      if (!result.await()) {
        println("A 출금 실패")
      } else {
        println("A 출금 성공")
      }
    }
  }

  // 모든 작업이 완료될 때까지 대기
  jobs.forEach { it.join() }

  // 최종 잔액 조회 및 출력
  val aBalance = CompletableDeferred<Int>()
  A.send(GetBalance(aBalance))
  val bBalance = CompletableDeferred<Int>()
  B.send(GetBalance(bBalance))
  println("A 최종 잔고: ${aBalance.await()}")
  println("B 최종 잔고: ${bBalance.await()}")
}