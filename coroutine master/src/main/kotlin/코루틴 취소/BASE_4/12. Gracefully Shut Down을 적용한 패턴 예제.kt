package org.coroutine.`코루틴 취소`.BASE_4

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * 코루틴이 취소되어도 메모리 해제를 보장하는 예제
 *
 */
fun main() = runBlocking {
  val file = File("test.txt")
  file.writeText("Hello, World!")
  val reader = BufferedReader(FileReader(file))

  val job = launch {
    try {
      repeat(1000) {
        val line = reader.readLine()
        if (line == null) return@repeat
        print(line)
        delay(500L)
      }
    } finally {
      // 코루틴 취소 상황에도 안전하게 자원 반환 처리
      withContext(NonCancellable) {
        println("clean up")
        reader.close()
        println("clean up end")
      }
    }
  }

  delay(1300L)
  println("job: I'm tired of waiting!")
  job.cancelAndJoin()
  println("done")
}