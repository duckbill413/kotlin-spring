package org.coroutine.Channel.BASE_5

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 여러 소비자가 채널에서 데이터를 받을 때나 여러 생산자가 채널에 데이터를 보낼 때
 * 특정 코루틴이 우선적으로 선택되거나 편향적이지 않고 대기중인 코루틴 중에서 랜덤하게
 * 선택되어 전달된다.
 */
fun main(): Unit = runBlocking {
  val channel = Channel<Int>()

  repeat(3) { id ->
    launch {
      for (msg in channel) {
        println("worker $id got $msg")
      }
    }
  }

  launch {
    repeat(9) {
      channel.send(it)
    }
    channel.close()
  }
}