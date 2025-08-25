/**
 * Flow는 코틀린의 비동기 스트림 처리를 위한 API입니다.
 * Flow는 여러 값을 순차적으로 내보내는 데이터 스트림을 처리하는데 사용됩니다.
 */
package org.coroutine.Flow.BASE_1

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking


fun main() = runBlocking {
  // flow 빌더를 사용하여 Flow를 생성합니다.
  // emit()으로 값을 방출하고 delay()로 시간 간격을 둡니다.
  flow {
    emit(1)    // 첫 번째 값 방출
    delay(300) // 300ms 대기
    emit(2)    // 두 번째 값 방출  
    delay(500) // 500ms 대기
    emit(3)    // 세 번째 값 방출
  }

  /**
   * 1. publisher : 데이터 생성자
   * 2. subscriber : 데이터 소비자
   * 3. processor : 중간 단계 연결자
   */

  // Flow는 콜드 스트림으로, collect를 호출할 때마다 새로운 스트림이 시작됩니다.
  val flow = flow {
    println("start")
    delay(500L)
    emit(1)
  }

  println("flow 정의")

  flow.collect {
    println("$it")
  }
  flow.collect {
    println("$it")
  }
}