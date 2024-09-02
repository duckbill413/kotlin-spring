package org.example.starter.jvmstatic

class HelloClass {
    companion object{
        @JvmStatic
        fun hello() = "Hello!"
    }
}

object HiObject {
    @JvmStatic
    fun hi() = "hi"
}

fun main() {
    // java 의 static method 와 유사하게 사용
    val hello = HelloClass.hello()
    val hi = HiObject.hi()

    // companion object 를 java class 에서 사용할때는 Companion 을 사용해야 사용할 수 있다.
    // object 는 INSTANCE 를 사용해야 한다.
    // kotlin 에서는 활용성을 향상시키기 위해 @JvmStatic 에노테이션을 사용하면 바로 사용가능하다.
}