package org.example.enum_class

enum class PaymentStatus(val label: String) {
    UNPAID("미지급") {
        override fun isPayable(): Boolean {
            return true
        }
    },
    PAID("지급완료") {
        override fun isPayable(): Boolean = false
    },
    FAILED("지급실패") {
        override fun isPayable(): Boolean = false
    },
    REFUNDED("환불") {
        override fun isPayable(): Boolean = false
    },
    ;

    abstract fun isPayable(): Boolean
}

enum class SellingStatus(val label: String) : Sellable {
    SELLING("판매중") {
        override fun isSellable(): Boolean {
            return true
        }
    },
    OUT_OF_STOCK("품절") {
        override fun isSellable(): Boolean {
            return false
        }
    },
    NOT_FOR_SALE("미판매") {
        override fun isSellable(): Boolean {
            return false
        }
    },
    ;
}

interface Sellable {
    fun isSellable(): Boolean
}

fun main() {
    println(PaymentStatus.REFUNDED)

    // 내부 abstract function
    if (PaymentStatus.UNPAID.isPayable()) {
        println("결재 가능 상태")
    }

    // 외부 interface function
    if (SellingStatus.SELLING.isSellable()) {
        println("판매중인 상품")
    }

    // valueOf
    val paymentStatus = PaymentStatus.valueOf("PAID")
    println(paymentStatus.label)

    // Enum 동등성 비교
    if (paymentStatus == PaymentStatus.PAID) {
        println("결재 완료 상태")
    }

    for (status in PaymentStatus.entries) {
        println("[${status}] (${status.label}) (name: ${status.name}) (ordinal: ${status.ordinal})")
    }
}