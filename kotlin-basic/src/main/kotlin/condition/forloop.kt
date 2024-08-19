package org.example.condition

fun main() {
    // 범위 연산자 .. 를 사용해 for loop  돌리기
    for (i in 0..3) { // 3을 포함
        print(i)
    }
    println()

    // until 을 사용해 반복
    // 뒤에 온 숫자는 포함하지 않는다.
    for (i in 0 until 3) {
        print(i)
    }
    println()

    // step 에 들어온 값 만큼 증가
    for (i in 0..5 step 2) {
        print(i)
    }
    println()

    // downTo를 사용해 반복하면서 값을 감소시킨다.
    for (i in 5 downTo 1) {
        print(i)
    }
    println()

    // 전달받은 배열을 반
    val numbers = arrayOf(1, 2, 3)
    for (number in numbers) {
        print(number)
    }
    println()
}