package org.coroutine.컨텍스트.BASE_1.`17`

import kotlinx.coroutines.*
import java.util.concurrent.Executors

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

fun main() = runBlocking<Unit> {
  log("start")

  // default dispatcher
  // 일반적인 작업에 사용되는 Dispatcher
  launch(Dispatchers.Default) {
    log("dispatchers.default")
  }

  // dispatcher io
  // 네트워크 작업에 좀 더 최적화 되어있음
  launch(Dispatchers.IO) {
    log("dispatchers.io")
  }

  // dispatcher main
  // 서버 개발자들은 사용하지 않음 => 안드로이드 개발자들이 UI 작업을 할 때 사용
//  launch(Dispatchers.Main) {
//    log("dispatchers.main")
//  }

  // dispatcher unconfined
  // 자주 사용되지는 않음 => 테스트 환경 정도에서 주로 활용
  launch(Dispatchers.Unconfined) {
    log("dispatchers.unconfined")
  }

  /**
   * Dispatcher 설정 추가
   *
   */
  launch(Dispatchers.Default + CoroutineName("default cor test")) {
    log("default")
  }

  val myDispatcher = Executors.newSingleThreadExecutor { r ->
    Thread(r, "custom-thread")
  }.asCoroutineDispatcher()

  launch(myDispatcher) {
    log("custom dispatcher")
  }

  val combined = Dispatchers.Default + CoroutineName("combined cor test")
  launch(combined) {
    log("combined")
  }

  launch(Dispatchers.Default + CoroutineName("default cor test")) {
    log("with contest duck")

    withContext(myDispatcher) {
      log("myDispatcher")
    }

    log("with context after")
  }

  // dispatcher 의 종료 필요
  myDispatcher.close()
}