package org.example.lazy_initialization

class LateInit {
    // nullable 이 아님에도 문제 발생 없음 (var 사용 가능)
    // type 은 non-null type 만 지원
    // spring 과 같은 프레임워크의 DI 기능을 염두하여 만들어진 기능
    lateinit var text: String
    fun printText() {
        // isInitialized property 는 class 내부에서만 사용 가능
        if (this::text.isInitialized) {
            println("이미 초기화됨")
        } else {
            text = "안녕하세요"
        }
        println(text)
    }
}

fun main() {
    val lateInit = LateInit()
    lateInit.printText()

    println("한번 더 실행")
    lateInit.printText()
}