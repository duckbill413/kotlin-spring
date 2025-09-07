package org.coroutine.동시성2.BASE_2.`36`

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger

//class Example {
//  /**
//   * 변수를 메모리에 적재하겠다고 명시
//   * 변수의 값을 Read 할 때마다 CPU Cache 에서 읽는 것이 아닌 메모리에서 읽어 온다.
//   */
//  @Volatile
//  var running: Boolean = false
//}

//fun main(): Unit = runBlocking {
//  var counter = 0
//
//  val jobs = List(100) {
//    launch(Dispatchers.Default) {
//      repeat(1000) {
//        counter++
//      }
//    }
//  }
//
//  jobs.forEach { it.join() }
//  println("Counter: ${counter}")
//}

// 동시성 문제가 개선된 코드
fun main(): Unit = runBlocking {
  val counter = AtomicInteger(0)

  val jobs = List(100) {
    launch(Dispatchers.Default) {
      repeat(1000) {
        counter.incrementAndGet()
      }
    }
  }

  jobs.forEach { it.join() }
  println("Counter: ${counter.get()}")
}