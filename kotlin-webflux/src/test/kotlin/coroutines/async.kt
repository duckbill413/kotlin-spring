package coroutines

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun sum(a: Int, b: Int) = a + b

/**
 * launch 의 경우 실행의 흐름을 조절하는데 유용하지만
 * 실행의 결과값을 반환할 수는 없음
 * async-await 를 이용해서 결과값을 가져올 수 있음
 */
fun main() = runBlocking<Unit> {
    val result1 = async {
        delay(100)
        sum(1, 3)
    }

    println("result1: ${result1.await()}")

    val result2 = async {
        delay(100)
        sum(2, 5)
    }

    println("result2: ${result2.await()}")
}