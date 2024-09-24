package coroutines

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//fun main() {
//    doSomething() // 컴파일 에러 발생
//}
suspend fun main() {
    doSomething2()
}

fun printHello() = println("Hello")

/**
 * suspend 함수는
 * 일시 중지 및 재개가 가능
 * suspend 함수는 일반 함수 호출 가능
 * 일반 함수는 일반적인 방법으로 suspend 함수 호출 불가
 * coroutineScope 와 Non-blocking 과의 차이점은
 * 현재 쓰레드가 블로킹되지 않고 동작
 */
suspend fun doSomething() {
    printHello()
}

suspend fun doSomething2() = coroutineScope {
    launch {
        delay(200L)
        println("World!")
    }
    launch {
        println("Hello ")
    }
}