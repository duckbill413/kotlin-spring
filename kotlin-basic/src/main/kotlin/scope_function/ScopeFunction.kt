package org.example.scope_function

/**
 * 스코프 함수의 종류
 * 함수명    수신자 객체 참조 방법    반환 값        확장 함수 여부
 * let        it              함수의 결과           O    객체를 생성하거나 사용하는 시점에서 이름을 부여하여 다양한 작업을 수행하고 결과를 반환
 * run        this            함수의 결과           O    객체를 생성하거나 사용하는 시점에서 다양한 작업을 수행한 후 결과를 반환
 * with       this            함수의 결과           X    객체 할당 이후 객체에게 다양한 작업을 함께 할 때 사용
 * apply      this            컨텍스트 객체         O    객체에 무언가를 적용할 때 사용
 * also       it              컨텍스트 객체         O    객체에게 명령을 내리기 직전에 추가적인 작업 수행
 */

class DatabaseClient {
    var url: String? = null
    var username: String? = null
    var password: String? = null

    fun connect(): Boolean {
        println("DB 접속 중 ...")
        Thread.sleep(1000)
        println("DB 접속 완료")
        return true
    }
}

class User(val name: String, val password: String) {
    fun validate() {
        if (name.isNotEmpty() && password.isNotEmpty()) {
            println("검증 성공!")
            return
        }

        println("검증 실패!")
    }

    fun printName() = println(name)
}

fun main() {
    let()
    run()
    with()
    apply()
    also()
}

private fun also() {
    User(name = "tony", password = "").also {
        it.validate()
        it.printName()
    }
}

private fun apply() {
    println("===== apply =====")
    // 컨텍스트 객체에 대한 내부 초기화등을 위해 사용
    val client = DatabaseClient().apply {
        url = "localhost:3306"
        username = "mysql"
        password = "1234"
        // let, run, with 는 함수의 마지막이 반환
    }.connect().run { println(this) }
}

private fun with() {
    println("===== with =====")
    val str = "안녕하세요"

    val length = with(str) { // with 는 확장함수가 아님
        length
    }
    println(length)

    val connected = with(DatabaseClient()) {
        url = "localhost:3306"
        username = "mysql"
        password = "1234"
        connect()
    }
    println(connected)
}

private fun run() {
    println("===== run =====")

    // run 을 사용하지 않은 기존의 코드
    val config = DatabaseClient()
    config.url = "localhost:3306"
    config.username = "mysql"
    config.password = "1234"
    println(config.connect())

    // run 을 사용한 코드
    val connected = DatabaseClient().run {
        url = "localhost:3306"
        username = "mysql"
        password = "1234"
        connect()
    }
    println(connected)
}

private fun let() {
    println("===== let =====")
    val str1: String? = null

    // let 은 null 이 아닌 경우만 동작
    str1?.let {
        println(it)
    }

    val str2: String? = "hi"
    str2?.let {
        println(it)
    }

    // depth 가 깊어질 경우 가독성이 안좋음
    val num1: Int? = str2?.let {
        println(it)

        val abc: String? = "abc"
        abc?.let {
            val def: String? = "def"
            def?.let {
                println("abcdef 가 null 이 아님")
            }
        }
        1234
    }
}