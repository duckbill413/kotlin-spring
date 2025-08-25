package org.coroutine.코루틴_자식들.BASE_2

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.coroutineContext

suspend fun log(msg: String) = println("[${coroutineContext}] $msg")

/**
 * Coroutine 로깅 예제
 */
fun main() = runBlocking<Unit> {
  log("start")

  // -Dkotlin.coroutines.debug 옵션을 활성화 하면 coroutine 이름을 스레드에 포함하여 실행시킬 수 있음

  launch(CoroutineName("Coroutine-1")) {
    log("start")
    delay(500L)
    log("end")
  }

  launch(CoroutineName("Coroutine-2")) {
    log("start")
    delay(500)
    log("end")
  }
}