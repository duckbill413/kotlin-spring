package org.coroutine.기초.BASE_1

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking{
  launch {
    delay(1000L)
    println("World!");
  }
  println("Hello,");
}