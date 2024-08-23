package org.example.generic_class

class MyGenerics<T>(val t: T) {

}

class SampleGeneric<T>(val t: T) {

}

// out 공변성 적용
class SampleGeneric2<out T>(val t: T) {

}

// in 반공변성 적용
class SampleGeneric3<T> {
    fun saveAll(
        to: MutableList<in T>,
        from: MutableList<T>
    ) {
        to.addAll(from)
    }
}

fun main() {
    // 제네릭을 사용한 클래스의 인스턴스를 만드려면 타입아규먼트를 제공
    val generics = MyGenerics<String>("테스트") // 생략도 가능

    // 변수의 타입에 제네릭을 사용한 경우
    val list1: MutableList<String> = mutableListOf()
    // 타입아규먼트를 생성자에서 추가
    val list2 = mutableListOf<String>()

    val list3: List<*> = listOf("테스트") // 정확한 타입을 지정하지 않고도 사용가능

    // 제네릭 변성
    // 파라미터화된 type이 서로 어떤 관계인지 설명하는 개념
    // 공변성, 반공변성, 무공변성으로 나뉨
    // PECS 규칙으로 구분할 수 있음
    // product -> extends, consumer -> super
    // 공변성은 자바 제네릭의 extends 코틀린에서는 out
    // 반공변성은 자바 제네릭의 super 코틀린에서는 in
    val sampleGeneric = SampleGeneric("테스트")

    // CharSequence 가 String의 상위 객체임에도 불구하고컴파일 에러가 발생
//    val charGeneric : SampleGeneric<CharSequence> = sampleGeneric

    // 클래스에 out 을 이용하여 공변성 적용
    val sampleGeneric2 = SampleGeneric2("테스트")
    val charGeneric2: SampleGeneric2<CharSequence> = sampleGeneric2

    // in 반공변성 적용
    val bag = SampleGeneric3<String>()
    bag.saveAll(mutableListOf<CharSequence>("1", "2"), mutableListOf<String>("3", "4"))

}