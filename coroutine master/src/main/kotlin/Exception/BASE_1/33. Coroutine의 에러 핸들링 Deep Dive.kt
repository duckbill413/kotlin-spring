package org.coroutine.Exception.BASE_1

import kotlinx.coroutines.*

fun main(): kotlin.Unit = runBlocking {
  println("일반 Job\n")
  val parent1 = CoroutineScope(Job())
  parent1.launch {
    launch {
      delay(50)
      println("Job1 throwing error")
      throw RuntimeException("This is a throwable from Job1")
    }

    launch {
      try {
        delay(200)
        println("Job2 completed")
      } catch (e: CancellationException) {
        println("Job2 Cancelled")
      }
    }
  }.join()

  delay(1000)


  println("\nSupervisorJob")
  val parent2 = CoroutineScope(SupervisorJob())
  val job1 = parent2.launch {
    launch {
      delay(50)
      println("Job1 throwing error")
      throw RuntimeException("This is a throwable from Job1")
    }
  }

  val job2 = parent2.launch {
    launch {
      try {
        delay(200)
        println("Job2 completed")
      } catch (e: CancellationException) {
        println("Job2 Cancelled")
      }
    }
  }.join()
}