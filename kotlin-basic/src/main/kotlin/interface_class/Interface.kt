package org.example.interface_class

class Product(val name: String, val price: Int)

// Cart 의 상위 interface
interface Wheel {
    fun roll()
}

interface Cart : Wheel {
    var coin: Int
    val weight: String
        get() = "20KG"

    fun add(product: Product)

    // rent 는 override 가 강제 아님
    fun rent() {
        if (coin > 0) {
            println("카트를 대여합니다")
        }
    }

    fun printId() = println("1234")
}

interface Order {
    fun add(product: Product) {
        println("${product.name} 주문이 완료되었습니다.")
    }

    fun printId() = println("54321")
}

// 복수의 인터페이스 구현 가능
class MyCart(override var coin: Int) : Cart, Order {
    override fun add(product: Product) {
        if (coin <= 0) println("코인을 넣어주세요")
        else println("${product.name}이 카트에 추가됐습니다.")

        // 주문하기
        super<Order>.add(product)
    }

    // 상위 interface 에 동일한 method 가 존재할때의 처리
    override fun printId() {
        super<Cart>.printId()
        super<Order>.printId()
    }

    override fun roll() {
        println("카트가 굴러갑니다")
    }

}

fun main() {
    val myCart = MyCart(coin = 100)
    myCart.rent()
    myCart.roll()
    myCart.printId()
    myCart.add(Product(name = "장난감", price = 1000))
}