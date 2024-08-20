package org.example.class_property

class Coffee(
    val name: String,
    val price: Int, // 후행 콤마
) {
    val brand: String
        get() = "스타벅스"

    var quentity: Int = 0
        set(value) {
            // 수량이 0 이상인 경우에만 할당
            if (value > 0) {
                field = value
            }
        }
        get() = field
}

class EmptyClass {

}

// constructor 은 생략 가능
class Animal constructor(
    var name: String = "",
    var count: Int = 0,
) {


}

fun main() {
    val animal: Animal = Animal()
    animal.name = "강아지"
    animal.count = 10
    println("${animal.name}: ${animal.count}마리")

    val coffee = Coffee(name = "스타벅스", price = 5000)
    println("${coffee.name}: 가격: ${coffee.price} 브랜드: ${coffee.brand} 수량: ${coffee.quentity}")
    coffee.quentity = 10
    println("${coffee.name}: 가격: ${coffee.price} 브랜드: ${coffee.brand} 수량: ${coffee.quentity}")
}