package org.example.extension_function

fun String.first(): Char {
    return this[0]
}

fun String.addFirst(char: Char): String {
    return char + this
}

class MyExample {
    fun printMessage() = println("클래스 출력")
}

fun MyExample.printMessage() = println("확장 출력") // 실행되지 않는다.

// null 가능성이 있는 클래스의 확장
fun MyExample?.printNullOrNotNull() {
    if (this == null) {
        println("널인 경우에만 출력")
        return
    }
    println("널이 아닌 경우 출력")
}

fun main() {
    println("ABCD".first())

    println("ABCD".addFirst('Z'))

    println(MyExample().printMessage())

    var myExample: MyExample? = null
    // 안전 연산자를 쓰지 않았는데도 에러 발생 안함
    // 해당 함수 내부에서 null 처리를 하고 있기 떄문
    myExample.printNullOrNotNull()
    myExample = MyExample()
    myExample.printNullOrNotNull()
}