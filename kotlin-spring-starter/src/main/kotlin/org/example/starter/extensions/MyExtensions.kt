package org.example.starter.extensions

fun String.first() : Char {
    return this[0]
}

fun String.addFirst(char: Char) : String {
    return char + this
}

fun main() {
    println("ABCD".first())
    println("ABCD".addFirst('Z'))
}