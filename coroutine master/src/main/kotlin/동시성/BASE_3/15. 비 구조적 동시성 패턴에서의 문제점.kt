package org.coroutine.동시성.BASE_3

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

suspend fun one() : Int {
  delay(1000L)
  return 13
}

suspend fun two() : Int {
  delay(1000L)
  return 29
}

/**
 * - 를 사용하여 글로벌 스코프에서 코루틴을 시작 `GlobalScope.async`
 * - 어노테이션은 위험할 수 있는 API 사용을 명시적으로 허용 `@OptIn(DelicateCoroutinesApi::class)`
 * - **비구조적 동시성**의 예: 부모 스코프와 독립적으로 실행되는 코루틴
 */
@OptIn(DelicateCoroutinesApi::class)
fun somethingOne() = GlobalScope.async { one() }


@OptIn(DelicateCoroutinesApi::class)  
fun somethingTwo() = GlobalScope.async { two() }
/**
 * 비구조적 동시성
 * 책임을 지지 않는 코루틴
 *
 * - `somethingOne()``somethingTwo()`과 는 `GlobalScope.async`를 사용합니다
 * - 이는 부모 코루틴()과 **독립적으로** 실행됩니다 `runBlocking`
 * - 부모가 취소되어도 이 코루틴들은 계속 실행될 수 있습니다
 * - 부모는 자식 코루틴들의 생명주기를 관리하지 않습니다
 *
 */
fun main() = runBlocking {
  val one = somethingOne()
  val two = somethingTwo()
  println("one + two = ${one.await() + two.await()}")
}

/**
 * 구조적 동시성을 지킨 형태
 *
 * - `coroutineScope` 블록을 사용하여 구조적 동시성 보장
 * - 부모-자식 관계가 명확하게 정의됨
 * - 모든 자식 코루틴이 완료될 때까지 대기
 * - 자식 코루틴에서 예외 발생 시 다른 모든 자식들이 취소됨
 * - 두 개의 비동기 작업(`one()`과 `two()`)을 동시에 실행
 * - `await()`를 사용하여 두 결과의 합을 반환
 */
suspend fun sum() : Int = coroutineScope {
  val one = async{ one()}
  val two = async{ two()}
  one.await() + two.await()
}