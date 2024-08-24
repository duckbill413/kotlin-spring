package org.example.lazy_initialization


class ByLazy {
    var greeting: String? = null // 가변 변수 형태
    fun sayHello() = println(greeting + "1")
}

class ByLazy2 {
    val greeting: String by lazy {
        println("초기화 로직 수행2")
        getHello()
    }

    fun sayHello() = println(greeting + "2")
}

class ByLazy3 {
    val greeting: String by lazy(LazyThreadSafetyMode.NONE) {
        println("초기화 로직 수행3")
        getHello()
    }

    fun sayHello() = println(greeting + "3")
}

fun getHello() = "안녕하세요"
fun main() {
    val byLazy = ByLazy()

    byLazy.greeting = getHello()
    byLazy.sayHello()

    // by-lazy 방식 지연 초기화 (val 변수에 사용 가능)
    // 변수를 사용 시점에 초기화 (초기화는 한번만 이루어짐)
    val byLazy2 = ByLazy2()

    // by-lazy 방식은 multi thread 환경에서도 안전
    for (i in 1..5) {
        Thread {
            byLazy2.sayHello()
        }.start()
    }

    /**
     * SYNCHRONIZED: 기본값으로, 여러 스레드에서 동시에 접근해도 하나의 스레드만 초기화를 수행하도록 보장합니다. 즉, 스레드 안전성을 제공합니다.
     *
     * PUBLICATION: 여러 스레드가 동시에 초기화할 수 있지만, 하나의 결과만 사용됩니다. 여러 스레드가 동시에 초기화할 가능성이 있지만, 그 중 하나의 결과만이 최종적으로 저장됩니다.
     *
     * NONE: 스레드 안전성을 보장하지 않습니다. 여러 스레드가 동시에 접근해도 안전하지 않으며, 이 모드는 하나의 스레드만 접근하는 상황에서만 사용해야 합니다.
     */
    // LazyThreadSafeMode 를 사용하지 않을 수 있음
    val byLazy3 = ByLazy3()
    for (i in 1..5) {
        Thread {
            byLazy3.sayHello()
        }.start()
    }
}