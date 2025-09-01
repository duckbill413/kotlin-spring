package org.coroutine.Channel.BASE_1

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


/**
 * 1. Rendezvous Channel - 버퍼가 없는 채널. 송신자와 수신자가 만날 때만 데이터 전송*
 * 2. Buffered Channel - 지정된 크기의 버퍼를 가진 채널. 버퍼가 가득 찰 때까지 송신 가능*
 * 3. Conflated Channel - 새로운 값이 이전 값을 대체하는 채널. 최신 값만 유지
 * 4. Unlimited Channel - 무제한 버퍼를 가진 채널. 메모리가 허용하는 한 계속 송신 가능
 *
 */

fun main(): Unit = runBlocking {
  val rendezvousChannel = Channel<Int>()
  val bufferedChannel = Channel<Int>(10)
  val conflatedChannel = Channel<Int>(Channel.CONFLATED)
  val unlimitedChannel = Channel<Int>(Channel.UNLIMITED)

  val channel = rendezvousChannel

  launch {
    for (i in 1..5) {
      println("sending $i")
      channel.send(i)
      delay(500)
    }
  }

  launch {
    for (v in channel) {
      println("received $v")
      delay(1000)
    }
  }
}