import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

// Subject (Barista)
class Barista2 {
    private val support = PropertyChangeSupport(this)
    private lateinit var coffeeName: String

    fun orderCoffee(name: String) {
        this.coffeeName = name
    }

    fun makeCoffee() {
        val coffee = Coffee(this.coffeeName)
        support.firePropertyChange("coffee", null, coffee)
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        support.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        support.removePropertyChangeListener(listener)
    }
}

// Observer (Customer)
class Customer2(val name: String) : PropertyChangeListener {
    override fun propertyChange(evt: PropertyChangeEvent?) {
        val coffee = evt?.newValue as Coffee
        println("${name}이 ${coffee.name}을 받았습니다.")
    }
}

fun main() {
    val barista = Barista2()
    barista.orderCoffee("아이스 아메리카노")

    val customer = Customer2("고객1")
    barista.addPropertyChangeListener(customer)
    barista.makeCoffee()
}
