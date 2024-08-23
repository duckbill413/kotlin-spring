package org.example.singleton

import java.time.LocalDateTime

// kotlin singleton object
object Singleton {
    val a = 1234

    fun printA() = println(a)
}

object DatetimeUtils {
    val now: LocalDateTime
        get() = LocalDateTime.now()

    const val DEFAULT_FORMAT = "YYYY-MM-DD"

    fun same(a: LocalDateTime, b: LocalDateTime): Boolean {
        return a == b
    }
}

fun main() {
    println(Singleton.a)
    Singleton.printA()

    println(DatetimeUtils.now)
    println(DatetimeUtils.DEFAULT_FORMAT)

    val now = LocalDateTime.now()
    println(DatetimeUtils.same(now, now))
}