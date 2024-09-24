package coroutines

import kotlinx.coroutines.runBlocking

/**
 * `Thread.currentThread().name`으로
 * @coroutine 을 확인하기 위해서는
 * Edit Configuration -> VM options -> `-Dkotlinx.coroutines.debug` 옵션 추가
 *
 * Test code 나 Spring batch 같은 경우는 coroutine 을 지원하지 않기 때문에
 * `runBlocking` 을 사용해서 coroutine 문제를 해결
 */
fun main() {

    //* Non-Blocking 으로 동작 *//
    runBlocking {
        println("Hello")
        println(Thread.currentThread().name)
    }

    println("World")
    println(Thread.currentThread().name)
}