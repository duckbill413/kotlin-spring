package org.coroutine.`코루틴 취소`.BASE_3

import kotlinx.coroutines.*

fun main() = runBlocking {
  val job = launch(Dispatchers.Default) {
    repeat(5) {i ->
      try {
        println("job: I'm sleeping $i ...")
        delay(500L)
      }
      // 에러 처리 로직 추가 (Coroutine 중지 에러 처리)
      catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        println(e)
      }
    }
  }

  delay(1300L)
  println("main: I'm tired of waiting!")
  job.cancelAndJoin()
  println("main: Now I can quit.")
}