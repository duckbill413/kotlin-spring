package org.coroutine.Exception.BASE_2

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope


/**
 * 코루틴의 예외 처리를 보여주는 예제
 * - 여러 코루틴의 독립적인 실행
 * - supervisorScope를 통한 예외 처리
 * - 예외 전파 동작 방식 시연
 */
fun main(): Unit = runBlocking {
  println("Coroutine 1 : Start")


  // 첫 번째 자식 코루틴: 500ms 동안 실행 후 정상 종료
  launch {
    println("Coroutine 2 : Start")
    delay(500)
    println("Coroutine 2 : End")
  }

  // 두 번째 자식 코루틴: 첫 번째와 동일하게 500ms 동안 실행
  launch {
    println("Coroutine 3 : Start")
    delay(500)
    println("Coroutine 3 : End")
  }

  /**
   * SupervisorJob 은 상위로 예외를 전파하지 않는다.
   * 자식으로는 전파를 진행
   *
   * 구조화된 동시성을 지키며 예외처리를 진행한 예시
   */
  // supervisorScope를 사용하여 예외를 격리
  // - Coroutine 4에서 발생하는 예외가 부모로 전파되지 않음
  // - Coroutine 5는 Coroutine 4의 자식이므로 예외의 영향을 받음
  supervisorScope {

    launch {
      println("Coroutine 4 : Start")

      // 중첩된 코루틴: 부모 코루틴이 예외로 취소되면 같이 취소됨
      launch {
        println("Coroutine 5 : Start")
        delay(500)
        println("Coroutine 5 : End")
      }

      delay(200)

      throw RuntimeException("Coroutine 4 : Error")
    }
  }

  delay(1000)
  println("Coroutine 1 : End")
}