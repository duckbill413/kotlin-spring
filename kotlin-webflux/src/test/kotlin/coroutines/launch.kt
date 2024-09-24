package coroutines

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

/**
 * 비동기로 동작하는 launch
 * launch 를 병렬적으로 수행
 * launch 는 Job 을 반환
 * launch 는 비동기 실행의 흐름을 설정하는데 유용
 */
fun main() = runBlocking<Unit> {
    launch {
        delay(1000L) // coroutine 에서 사용 Thread 를 차단하지 않고 중단
//        Thread.sleep(500) // 기존의 코드 Thread 가 blocking 됨
        println("World!")
    }

    println("Hello")

    val job1 = launch {
        val elapsedTime = measureTimeMillis {
            delay(150)
        }
        println("async task-1 $elapsedTime ms")
    }
    job1.cancel()

    val job2 = launch(start = CoroutineStart.LAZY) {
        val elapsedTime = measureTimeMillis {
            delay(100)
        }
        println("async task-2 $elapsedTime ms")
    }
    println("start task-2")
    job2.start()
}