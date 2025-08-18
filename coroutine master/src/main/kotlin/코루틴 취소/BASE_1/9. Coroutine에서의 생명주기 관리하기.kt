package org.coroutine.`코루틴 취소`.BASE_1

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
  val job = launch {
    repeat(1000) { i ->
      println("I'm sleeping $i ...")
      delay(500L)
    }
  }

  delay(1300L)
  println("I'm tired of waiting!")
  job.cancelAndJoin()
//  job.cancel()  // 명시적으로 코루틴 종료
//  job.join()
  println("out!")
}