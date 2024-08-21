package org.example.collection

import java.util.*

fun main() {

    // immutable list: 최초의 값 할당 이후 값 변경이 불가
    val currencyList = listOf("달러", "유로", "원")

    // mutable list
    val mutableCurrencyList = mutableListOf<String>().apply {
        add("달러")
        add("유로")
        add("원")
    }
    mutableCurrencyList.add("엔")

    // immutable set
    val numberSet = setOf(1, 2, 3, 4)

    // mutable set
    val mutableNumberSet = mutableSetOf<Int>().apply {
        add(1)
        add(2)
        add(3)
        add(4)
    }

    // immutable map
    val numberMap = mapOf("one" to 1, "two" to 2)

    // mutable map
    val mutableNumberMap = mutableMapOf<String, Int>().apply {
        put("one", 1)
    }
    mutableNumberMap["two"] = 2

    // collection builder
    // MutableList<Int> 로 생성됨
    // buildList은 내부에서는 mutable 하지만 생성된 list 는 immutable 하다
    val numberList = buildList {
        add(1)
        add(2)
        add(3)
    }

    // 각 구현체의 기능을 내부에서 사용 가능
    val linkedList = LinkedList<Int>().apply {
        addFirst(3)
        add(2)
        addLast(1)
    }

    // iterator 의 사용
    var iterator = currencyList.iterator()
    while (iterator.hasNext()) {
        println(iterator.next())
    }
    println()

    for (currency in currencyList) {
        println(currency)
    }

    // it lambda 의 사용
    currencyList.forEach { println(it) }

    // for loop -> map
    // map 사용 x
    val lowerList = listOf("a", "b", "c")
    var upperList = mutableListOf<String>()
    for (lowerCase in lowerList) {
        upperList.add(lowerCase.uppercase())
    }
    println(upperList)

    // map 사용 o
    val upperCaseList = lowerList.map { it.uppercase() }
    println(upperCaseList)

    // filter 의 사용법
    val filteredList = mutableListOf<String>()
    // filter 사용 x
    for (upperCase in upperCaseList) {
        if (upperCase == "A" || upperCase == "C") {
            filteredList.add(upperCase)
        }
    }
    println(filteredList)

    // filter 사용 o
    val filteredListWithFilter = upperList.filter { it == "A" || it == "C" }
    println(filteredListWithFilter)

    // sequence filter list
    // filter 절을 여러개 사용하더라도 마지막 toList 부분에서만 list 를 생성하기 때문에 메모리 효율적
    val sequenceFilterList = upperList.asSequence()
        .filter { it == "A" || it == "C" }
        .filter { it == "C" }
        .toList()
    println(sequenceFilterList)
}
