package org.example.exception

fun main() {
    // kotlin 에서는 catch exception 도 try~catch 가 필수가 아님
    Thread.sleep(1)

    // try~catch~finally 의 사용
    try {
        Thread.sleep(1000)
    } catch (e: Exception) {
        println("에러 발생")
    } finally {
        println("finally 실행!")
    }

    val a = try {
        "1234".toInt()
    } catch (e: Exception) {
        println("에러 발생!")
    }
    println(a)

    // failFast Exception 을 사용하여 null 안정성 보장
    var b: String? = "널이 아님"
    var c = b ?: failFast("b is null")
    println(c.length) // c?.length 로 사용하지 않아도 compile error x

    // 직접 에러 발생 시키기
    throw NullPointerException("null 에러 발생")
}

fun failFast(message: String): Nothing {
    throw IllegalArgumentException(message)
}
