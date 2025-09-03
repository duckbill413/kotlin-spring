package org.coroutine.Channel.BASE_3

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

/**
 * Fan Out 패턴은 하나의 생성자가 새로운 데이터를 여러 소비자에게 분산해서 처리하도록 하는 패턴
 * 즉, 생성자는 하나인데 여러 코루틴이 병렬적으로 소비하는 패턴
 * 로드밸런서와 같은 형태로 사용된다.
 * 작업큐 나 워커풀에 많이 사용된다.
 * producer - processor - consumer 의 형태는 동일하며 consumer 만 여러개인 형태로 구성
 * 단점)
 * 1. 처리 순서를 보장할 수 없음
 * 2. 브로드캐스팅 하는 형태와는 조금 다름. 채널을 구독하는 여러 소비자들에게 전달해주는 것이 아니라 반드시 하나의 메시지만 단 하나의 소비자에게 전달되는 구조
 */

data class Job(val id: Int, val number: Int)
data class Result(val jobId: Int, val number: Int, val isPrime: Boolean)

fun isPrime(n: Int): Boolean {
  if (n < 2) return false
  for (i in 2..Math.sqrt(n.toDouble()).toInt()) {
    if (n % i == 0) return false
  }
  return true
}

fun main(): Unit = runBlocking {
  val jobChannel = Channel<Job>(Channel.UNLIMITED)
  val resultChannel = Channel<Result>(Channel.UNLIMITED)

  val consumerCount = 4
  val jobCount = 5

  launch {
    repeat(jobCount) { id ->
      val number = Random.nextInt(10_000, 10_100)
      println("생성자: Job($id, $number) 생성")
      jobChannel.send(Job(id, number))
    }

    jobChannel.close()
  }

  repeat(consumerCount) { workId ->
    launch {
      for (job in jobChannel) {
        val result = Result(job.id, workId, isPrime(job.number))
        println("소비자 $workId: 작업: ${job.id}, 숫자: ${job.number}")
        delay(1000)
      }
    }
  }
}