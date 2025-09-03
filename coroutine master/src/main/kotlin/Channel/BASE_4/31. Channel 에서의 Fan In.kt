package org.coroutine.Channel.BASE_4

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Fan In 은 Fan out 의 반대 개념
 * Fan In 은 Consumer 가 한명이고 Producer 가 여러명인 형태
 */

fun main(): Unit = runBlocking {
  val channel = Channel<String>()
  val producerCount = 3

  repeat(producerCount) { idx ->
    launch {
      repeat(5) { msg ->
        channel.send("Producer $idx sends message $msg")
        delay(100L + idx * 120L)
      }
    }
  }

  for (msg in channel) {
    println("consumer received $msg")
  }
}