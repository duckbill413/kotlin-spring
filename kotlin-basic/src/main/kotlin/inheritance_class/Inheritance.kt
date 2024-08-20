package inheritance_class

open class Dog {
    open var age: Int = 0
    open fun bark() {
        println("멍멍")
    }
}
class Bulldog : Dog () {
    override fun bark() {
        println("컹컹")
    }
}

// 기본 생성자의 사용 (override 를 이용하여 상위 클래스의 변수 할당)
open class Maltese(override var age : Int=0) : Dog() {
    // final 을 사용하면 하위에서 상속 불가
    final override fun bark() {
        super.bark()
    }
}

class ChildMaltese : Maltese() {
    override var age: Int = 0

    // 에러 발생
//    override fun bark() {
//        super.bark()
//    }
}

abstract class Developer {
    abstract var age : Int
    abstract fun code(language: String)

    fun todo() {
        println("Hard Working")
    }
}

class BackendDeveloper(override var age: Int) : Developer() {
    override fun code(language: String) {
        println("I code with $language")
    }

}
fun main() {
    val maltese = Maltese(age = 2)
    println(maltese.age)
    maltese.bark()

    val backendDeveloper = BackendDeveloper(20)
    println(backendDeveloper.age)
    backendDeveloper.code("kotlin")
    backendDeveloper.todo()
}