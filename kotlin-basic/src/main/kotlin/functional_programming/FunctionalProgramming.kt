package org.example.functional_programming

// 함수는 1급 객체
// 함수는 데이터이다.
val func1: () -> String = { "duckbill" }
val printHello: () -> Unit = { println("hello") }

// 함수를 인자로 받아 실행 하는 함수
fun call(block: () -> Unit) {
    block()
}

// fun 으로 선언한 함수는 1급 객체의 특징을 가지지 못한다.
// 객체로 저장하거나 변수로 사용할 수 없다.
fun printNo() = println("No!")
val printMsg: (String) -> Unit = { message: String -> println(message) }

val printMessage: (message: String) -> Unit = { println(it) }
val printMessage1: (String) -> Unit = { println(it) }
val plus: (Int, Int) -> Int = { a, b -> a + b }

// 고차 함수 (함수를 인자로 받거나, 결과로 주는 함수)
fun forEachStr(collection: Collection<String>, action: (String) -> Unit) {
    for (item in collection) {
        action(item)
    }
}

// 익명 함수 (이름 없는 함수)
fun outerFunc(): () -> Unit {
    // 익명 함수 형태 1
//    return fun() {
//        println("이것은 익명함수!")
//    }
    // 익명 함수 형태 2
    return {
        println("이것은 익명함수!")
    }
}

// 익명 함수 형태 3
fun anonymousFunc(): () -> Unit = { println("이것은 익명함수!!!!!!!!") }

// 람다 레퍼런스
val callReference = ::printHello

// 최대한 생략한 람다식
val sum = { x: Int, y: Int -> x + y }
fun main() {
    val list = mutableListOf(printHello)
    list[0]()
    call(list[0])

//    call(printNo()) // 컴파일 오류가 발생한다.

    printMsg("message0")
    printMessage("message1")
    println(plus(1, 3))

    val listOf = listOf("a", "b", "c")
    val printStr: (String) -> Unit = { println(it) }
    forEachStr(listOf, printStr)

    outerFunc()() // 익명 함수의 실행

    anonymousFunc()()

    callReference()() // 람다 레퍼런스 실행

    val numberList = listOf("1", "2", "3")
    numberList.map(String::toInt).forEach(::println)
}