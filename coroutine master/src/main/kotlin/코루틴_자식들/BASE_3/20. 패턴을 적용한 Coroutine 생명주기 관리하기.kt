package org.coroutine.코루틴_자식들.BASE_3

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


/**
 * MyActive 클래스는 코루틴 생명주기를 관리하는 클래스입니다.
 * CoroutineScope를 구현하여 코루틴의 실행 범위를 정의합니다.
 */
class MyActive : CoroutineScope {
  // SupervisorJob을 사용하여 자식 코루틴의 실패가 다른 자식에게 영향을 주지 않도록 함
  private val activeJob = SupervisorJob()

  // 코루틴의 실행 컨텍스트를 설정 (SupervisorJob + Default 디스패처)
  // Default Dispatcher 를 설정시 공용으로 사용이 되는 스레드 풀을 사용
  override val coroutineContext: CoroutineContext get() = activeJob + Dispatchers.Default

  /**
   * 두 개의 독립적인 코루틴을 시작하는 메서드
   * 하나는 데이터 패치를 시뮬레이션하고, 다른 하나는 애니메이션을 시뮬레이션합니다.
   */
  fun startOperation() {
    launch {
      println("[${Thread.currentThread().name} 데이터 패치 사작]")
      delay(1000)
      println("[${Thread.currentThread().name} 데이터 패치 종료]")
    }

    launch {
      println("[${Thread.currentThread().name} 애니메이션 사작]")
      delay(3000)
      println("[${Thread.currentThread().name} 애니메이션 종료]")
    }
  }

  /**
   * 모든 자식 코루틴을 취소하고 리소스를 정리하는 메서드
   */
  fun destroy() {
    println("모든 코루틴 종료")
    activeJob.cancel()
  }
}

/**
 * 코루틴 생명주기 관리 예제를 실행하는 메인 함수
 * MyActive 인스턴스를 생성하고 작업을 시작한 후 일정 시간 후에 종료합니다.
 */
fun main(): Unit = runBlocking {
  println("예제 시작")

  val active = MyActive()
  active.startOperation()

  delay(4500L)
  active.destroy()

  println("예제 종료")
}