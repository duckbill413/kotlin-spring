package org.example.sealed_class

sealed class Developer {
    abstract val name: String
    abstract fun code(language: String)
}

data class Backend(override val name: String) : Developer() {
    override fun code(language: String) {
        println("저는 백엔드 개발자입니다. ${language}를 사용합니다.}")
    }
}

data class Frontend(override val name: String) : Developer() {
    override fun code(language: String) {
        println("저는 프론트엔드 개발자입니다. ${language}를 사용합니다.}")
    }
}

object DeveloperPool {
    val pool = mutableMapOf<String, Developer>()

    fun add(developer: Developer) = when (developer) {
        is Backend -> pool[developer.name] = developer
        is Frontend -> pool[developer.name] = developer
//        else -> println("지원하지 않는 개발자입니다.")
    }

    fun get(name: String) = pool[name]
}

fun main() {
    val backend = Backend(name = "토니")
    DeveloperPool.add(backend)
    val frontend = Frontend(name = "수지")
    DeveloperPool.add(frontend)

    println(DeveloperPool.get("토니"))
    println(DeveloperPool.get("수지"))
}