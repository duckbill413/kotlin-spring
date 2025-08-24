package org.coroutine.코루틴_자식들.BASE_1

import kotlinx.coroutines.*

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")


/**
 * 코루틴의 부모-자식 관계를 보여주는 예제:
 * 1. 기본 launch로 생성된 코루틴 [A]는 부모(runBlocking)의 자식이 되어 예외 발생 시 부모에게 전파됨
 * 2. GlobalScope.launch로 생성된 코루틴 [B]는 부모와 독립적으로 실행되어 예외나 취소가 전파되지 않음
 * 3. launch(Job())으로 생성된 코루틴 [C]는 새로운 Job을 가지므로 부모와 독립적으로 실행됨
 *
 * 실행 결과:
 * - [A]의 예외가 부모에게 전파되어 전체 코루틴 스코프가 취소됨
 * - [B]와 [C]는 독립적이므로 계속 실행되어 정상 종료됨
 */
fun main() {
  lateinit var globalJob: Job
  lateinit var customJob: Job

  try {
    runBlocking {
      log("parent - child 시작")

      launch {
        log("[A] 자식 코루틴 시작")
        delay(500)
        throw RuntimeException("[A] 예외 발생")
      }

      globalJob = GlobalScope.launch {
        try {
          log("[B] Global Scope 시작 부모와 무관")
          delay(2000)
          log("[B] Global Scope 종료")
        } catch (e: CancellationException) {
          log("[B] 전파 취소 감지 됨")
        }
      }

      // 새로운 Job 을 생성하여 부모와 독립적
      customJob = launch(Job()) {
        try {
          log("[C] 새로운 Job 으로 시작")
          delay(2000)
          log("[C] 새로운 Job 종료 됨")
        } catch (e: CancellationException) {
          log("[C] 전파 취소 감지됨")
        }
      }

      delay(1500)
    }
  } catch (e: Exception) {
    log("run ended ${e.message}")
  }

  runBlocking {
    globalJob.join()
    customJob.join()
    log("모든 코루틴 종료")
  }
}