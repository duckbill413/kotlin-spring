/**
 * Channel Pipeline 예제
 * - Producer, Processor, Consumer 패턴을 구현한 예제
 * - Channel을 통해 데이터가 순차적으로 처리되는 과정을 보여줌
 */
package org.coroutine.Channel.BASE_2.`29`

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * producer (생산자)
 * - 1부터 5까지의 숫자를 생성하여 채널에 전송
 * - 100ms 간격으로 숫자를 전송
 * - 모든 숫자를 전송한 후 채널을 닫음
 *
 * Entity/DTO 패턴과의 유사성:
 * - Entity처럼 데이터의 원천(source)이 되는 부분
 * - 실제 데이터를 생성하고 초기화하는 역할 수행
 */
fun CoroutineScope.produceNumbers(): Channel<Int> {
  val channel = Channel<Int>()
  launch {
    for (x in 1..5) {
      println("produce: $x")
      channel.send(x)
      delay(100L)
    }
    channel.close()
  }
  return channel
}

/**
 * processor (가공자)
 * - 입력 채널에서 숫자를 받아 제곱 연산을 수행
 * - 각 연산 사이에 300ms 딜레이를 줌
 * - 처리된 결과를 새로운 채널로 전송
 * - 딜레이를 걸었을때도 동기적 처리가 이루어짐을 알 수 있음
 *
 * Entity/DTO 패턴과의 유사성:
 * - DTO 변환처럼 데이터를 다른 형태로 가공하는 역할
 * - 입력 데이터를 비즈니스 로직에 맞게 변환하여 전달
 */
fun CoroutineScope.square(numbers: Channel<Int>): Channel<Int> {
  val channel = Channel<Int>()
  launch {
    for (x in numbers) {
      println("square: $x to ${x * x}")
      channel.send(x * x)
      delay(2000L)
    }
    channel.close()
  }
  return channel
}

fun main(): Unit = runBlocking {
  val numbers = produceNumbers()
  val squares = square(numbers)

  /**
   * consumer (소비자)
   * - squares 채널에서 처리된 결과값을 순차적으로 받아 출력
   * - 채널이 닫힐 때까지 값을 수신
   */
  for (sq in squares) {
    println("consumer: $sq\n")
  }
}