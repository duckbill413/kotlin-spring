package org.coroutine.동시성.BASE_4

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


/**
 * 코루틴의 부모-자식 관계와 에러 전파를 보여주는 예제
 * Parent-child relationship and error propagation demonstration in coroutines
 */

// 메인 함수에서 sum() 호출 후 발생하는 예외를 처리
// Main function calls sum() and handles any exceptions
fun main(): kotlin.Unit = runBlocking {
  try {
    sum()
  } catch (e: Exception) {
    println("ended in main $e")
  }
}

// coroutineScope를 사용하여 구조화된 동시성 구현
// 자식 코루틴에서 발생한 예외는 부모로 전파됨
suspend fun sum(): Int = coroutineScope {
  // 첫 번째 비동기 작업: Long.MAX_VALUE 만큼 대기 후 42 반환
  // 예외 발생 시 취소될 수 있음
  val one = async {
    try {
      delay(Long.MAX_VALUE)
      println("delay ended")
      42
    } finally {
      println("job cancelled")
    }
  }

  // 두 번째 비동기 작업: 즉시 ArithmeticException 발생
  // 이 예외는 부모 코루틴으로 전파되어 다른 자식 코루틴들도 취소됨
  val two = async<Int> {
    println("second job")
    throw ArithmeticException()
  }

  // 두 비동기 작업의 결과를 더하려 시도
  one.await() + two.await()
}