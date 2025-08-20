package org.coroutine.동시성.BASE_1

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

suspend fun one() : Int {
  delay(1000L)
  return 13
}

suspend fun two() : Int {
  delay(1000L)
  return 29
}

fun main() = runBlocking {
  // 블로킹 형태로 구현
  println("블로킹 형태")
  var time = measureTimeMillis {
    val one = one()
    val two = two()
    println("one + two = ${one + two}")
  }

  println("Completed in $time ms\n")

  // async, deferred 구현
  println("논블로킹 형태")
  time = measureTimeMillis {
    val deferredOne = async { one() }
    val deferredTwo = async { two() }
    println("one + two = ${deferredOne.await() + deferredTwo.await()}")
  }
  println("Completed in $time ms")
}