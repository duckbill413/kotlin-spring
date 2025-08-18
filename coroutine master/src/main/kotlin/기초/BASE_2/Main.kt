package org.coroutine.기초.BASE_2

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlin.random.Random

/**
 * 코루틴 사용법을 보여주는 Kotlin 애플리케이션의 진입점입니다.
 *
 * 이 `main` 함수는 `runBlocking`을 사용하여 코루틴을 초기화하고,
 * `launch`를 통해 새로운 코루틴을 실행하며, `delay`와 `join` 메서드의 사용법을 보여줍니다.
 *
 * 먼저 "Hello,"를 출력한 다음, 1초 동안 대기한 후 "World!"를 출력하는 코루틴이 완료될 때까지
 * 기다립니다. 마지막으로 코루틴이 완료된 후 "done"을 출력합니다.
 *
 * kotlin 은 스택 할당이 없고
 * 메모리가 아니기 때문에 메모리 사용이 적다는 특징을 가진다.
 * Thread 처럼 실행 단위가 아니라 비동기적으로 작업의 단위라는 특징
 *
 */

/**
 * 7. JVM Thread vs Coroutine 및 Job 객체 다루기
 *  * runBlocking
 *  * 다른 스코프가 가져가지 못하게 스레드를 점유
 */
//fun main() = runBlocking{
//    val job = launch {
//        delay(1000L)
//        println("World!")
//    }
//
//    repeat(5000    ) {
//      Thread {
//        Thread.sleep(1000L)
//        println("test")
//      }.start()
//    }
//    println("Hello,")
//    job.join()
//    println("done")
//}

//-------------------------
//fun main() = runBlocking{
//  doWork2()
//}
//
//suspend fun doWork() = coroutineScope {
//  launch {
//    delay(1000L)
//    println("World!")
//  }
//  println("Hello,")
//}
//
//suspend fun doWork2() = coroutineScope {
//  launch {
//    delay(2000L)
//    println("World2!")
//  }
//  launch {
//    delay(1000L)
//    println("World1!")
//  }
//  println("Hello,")
//}

//-------------------------
fun main() = runBlocking{
  val mock = listOf(1, 2, 3, 4, 5);
  try {
    val result = supervisorScope {
      mock.map { id ->
        async {          fetch(id)        }
      }.mapNotNull { deferred ->
        try {
          deferred.await()
        } catch (e: Exception) {
          // TODO 자원을 반환
          println("exception occurred: ${e.message}")
          null
        }
      }
    }
    println(result)
  } catch (e: Exception) {
    println("error: ${e.message}")
  }
}

suspend fun fetch(id: Int) : String{
  delay(Random.nextLong(500, 1500))
//  delay(1000L)
  if (Random.nextBoolean()) throw RuntimeException("failed to fetch")
  return "fetchResult(id=$id)"
}