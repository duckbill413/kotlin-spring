package org.example.nullsafety

import java.util.Queue

fun getNullStr(): String? = null
fun getLengthIfNotNull(str: String?) = str?.length ?: 0 // Elvis Operation
fun main() {

    // kotlin 은 null 을 compile 단계에서 검사한다.
//    val a : String = null
//    var b: String = "aabbcc"
//    b = null

    // nullable type
    var a: String? = null
//    a.length // null error 발생

    var b: String? = null
    println("b:  ${b?.length}") // 안전 연산자

    // if..else 로 검증
    var c: Int = if (b != null) b.length else 0
    println("c: $c")

    // kotlin 의 처리 방식
    var d: Int = b?.length ?: 0
    println("d: $d")

    val nullableStr = getNullStr()
    val nullableStrLength = getLengthIfNotNull(nullableStr)
    println("nullableStrLength: $nullableStrLength")

    val e: String? = null
    val f = e!!.length // !! 단언 연산자
    println(f) // NPE 의 발생
}