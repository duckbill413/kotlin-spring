package org.example.data_class

// data class 는 getter, setter, equals, hashcode 등이 구현되어 있음
data class Person(val name: String, val age: Int)

fun main() {
    val person1 = Person(name = "tony", age = 12)
    val person2 = Person(name = "tony", age = 12)

    println(person1 == person2)
    println(person1)

    // copy: copy 를 사용했을때 객체 불변성을 유지하는데 도움이 됨
    val person3 = person2.copy(name = "john")
    println(person3)

    // componentN
    println("이름=${person1.component1()}, 나이=${person2.component2()}")
    val (name, age) = person1 // 구조 분해 할당
    println("$name $age")
}