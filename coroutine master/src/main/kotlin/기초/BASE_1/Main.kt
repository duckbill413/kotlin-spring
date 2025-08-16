package org.coroutine.기초.BASE_1

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlin.random.Random

//fun main() = runBlocking{
//  launch {
//    delay(1000L)
//    println("World!");
//  }
//  println("Hello,");
//}

/**
 * 구조화된 동시성 패턴
 * - 코루틴 간의 생명주기를 개별적으로 명확하게 관리가 되어야 하는 패턴
 * - 코루틴 작업을 할때 가장 쉽게 발생할 수 있는 문제는 리소스 누수가 있음
 * - 부모와 자식간의 관계를 구성하고 하위에서 발생한 에러를 전파 받고 다시 송출하며 안전하게 처리를 하는 패턴을 주로 구성
 */
// main 함수 전체가 구조화된 동시성의 범위
fun main() = runBlocking{
 val mock = listOf(1, 2, 3, 4, 5);
  try {
    // supervisorScope 를 이용하여 coroutine 간의 에러 전파를 방지
    val result = supervisorScope {
      mock.map { it -> async { apiFetch()} }.mapNotNull { it.await() }
    }
  } catch (e: Exception) {
    println("error: ${e.message}")
  }
}

suspend fun apiFetch() {
  delay(Random.nextLong(500, 1500))
}