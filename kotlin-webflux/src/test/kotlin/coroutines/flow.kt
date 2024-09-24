package coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

/**
 * kotlin 에서 reactive programming style로 작성할 수 있도록
 * 만들어진 API
 * coroutine 의 suspend 는 단일 값 반환
 * flow 는 multi-return-values
 */
fun main() = runBlocking<Unit> {
    val flow = simple()
    println(flow)
    flow.collect { println(it) } // collect 동작 시점에 flow 코드가 동작
}

fun simple(): Flow<Int> = flow {
    println("Flow started")

    for (i in 1..3) {
        delay(200)
        emit(i) // 데이터를 통제
    }
}