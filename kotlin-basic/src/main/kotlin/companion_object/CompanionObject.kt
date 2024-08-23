package org.example.companion_object

// 동반 객체
class MyClass private constructor() {
    companion object {
        val a = 1234

        fun newInstance() = MyClass()
    }
}

class MyClass2 private constructor() {
    companion object Comp {
        val a = 1234

        fun newInstance() = MyClass2()
    }
}


fun main() {
    println(MyClass.Companion.a)
    println(MyClass.Companion.newInstance())

    println(MyClass.a) // Companion 생략
    println(MyClass.newInstance()) // Companion 생략

    println(MyClass2.Comp.a) // Naming Companion (잘 사용되지 않음)
}