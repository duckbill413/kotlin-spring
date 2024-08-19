package org.example.condition

fun main() {
    // if..else 사용
    val job = "Software Developer"
    if (job == "Software Developer") {
        println("개발자")
    } else {
        println("개발자 아님")
    }

    // 코틀린의 if..else 는 표현식이다.
    val age = 10
    val str = if (age > 10) {
        "성인"
    } else {
        "아이"
    }
    println(str)

    // 코틀린은 삼항 연산자가 없다. if..else 가 표현식이므로 불필요
    val a = 1
    val b = 2
    val c = if (b > a) b else a
    println(c)
}