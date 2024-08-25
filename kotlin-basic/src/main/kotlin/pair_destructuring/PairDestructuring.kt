package org.example.pair_destructuring

fun plus(a: Int, b: Int) = a + b
data class Tuple(val a: Int, val b: Int)

fun plus(tuple: Tuple) = tuple.a + tuple.b
fun plus(pair: Pair<Int, Int>) = pair.first + pair.second
fun plus(triple: Triple<Int, Int, Int>) = triple.first + triple.second + triple.third

data class SampleDataClass(val a: Int, val b: String)

fun main() {
    println(plus(1, 3))
    println(plus(Tuple(1, 3)))

    // Pair 사용
    // Pair 의 value 는 constant
    // pair 에서는 componentN 과 toList() 등도 지원
    val pair = plus(Pair(1, 3))
    println(pair)

    // triple 사용
    val triple = plus(Triple(1, 2, 3))
    println(triple)

    // pair, triple 을 이용한 구조분해 할당
    val (a, b, c) = Triple(4, 5, 6)
    println("$a $b $c")

    val (num, str) = SampleDataClass(10, "sample data")
    println("$num $str")

}