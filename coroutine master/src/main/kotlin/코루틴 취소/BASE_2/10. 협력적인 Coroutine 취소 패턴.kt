package org.coroutine.`코루틴 취소`.BASE_2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

fun main() = runBlocking {
  val startTime = System.currentTimeMillis()
  val job = launch(Dispatchers.Default) {
    var nextPrintTime = startTime
    var i= 0

    /**
     * job 의 cancelAndJoin 이 발생했을때
     * 내부 로직을 취소 시켜주는 부분이 존재하지 않음
     * while 문은 coroutine 에서 권장되지 않음
     */
    while (true) {
      if (System.currentTimeMillis() >= nextPrintTime) {
        println("job: I'm sleeping ${i++} ...")
        nextPrintTime += 500L
      }
      yield() // while 문을 사용할 경우 yield 를 사용하여 발생하는 cancel exception 을 처리
    }
  }

  delay(3000L)
  println("main: I'm tired of waiting!")
  job.cancelAndJoin()
  println("main: Now I can quit.")
  delay(3000L)
}