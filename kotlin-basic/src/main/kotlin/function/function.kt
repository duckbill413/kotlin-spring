package wh.duckbill.function

fun sum(a: Int, b: Int): Int {
    return a + b
}

fun sum2(a: Int, b: Int): Int = a + b

fun sum3(a: Int, b: Int) = a + b

// 몸통이 있는 함수는 반환 타입 생략 불가
//fun sum4(a: Int, b: Int) {
//    return a + b
//}

// 반환 타입이 없는 경우에는 Unit 을 반환
fun printSum(a: Int, b: Int): Unit {
    println("$a + $b = ${a + b}")
}

// 디폴트 파라미터
fun greeting(message: String = "안녕하세요!") {
    println(message)
}

fun log(level: String = "INFO", message: String) {
    println("[$level]$message")
}

fun log2(level: String = "INFO", message: String, noti: String) {
    println("[$level]$message||$noti")
}

fun main() {
    greeting()
    greeting("HIHI")

    println()

    log(message = "인포 로그")
    log(level = "DEBUG", "디버그 로그")
    log("WARN", "워닝 로그")
    log(level = "ERROR", message = "에러 로그")
}