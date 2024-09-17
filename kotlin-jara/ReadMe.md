# 비동기-논블로킹 프로그래밍

## 비동기 프로그래밍

### 1.1 동기 프로그래밍

- 동기(Synchronous)방식의 프로그램에서 작업의 실행 흐름은 순차적으로 동작

```kotlin
fun main() {
    val stock: StockDto = getRealtimeStockData("APPLE")

    println("주식 심볼: ${stock.symbol}")
    println("시가: ${stock.price.open}")
    println("종가: ${stock.price.close}")
    println("시가 총액: ${stock.price.marketCap}")
}
```

- 순차적으로 동작하는 프로그램은 코드를 파악하기 쉽고 결과를 예측하기 쉬우므로 디버깅이 쉽다.
- 특정 작업을 실행하는 동안에는 다른 작업을 할 수 없다는 단점이 존재

### 1.2 비동기 프로그래밍

- 비동기(Asynchronous) 방식의 프로그램에서 작업의 실행 흐름은 기본적으로 순차적이지 않다.
- 이러한 특징으로 인해 비동기 처리 방식은 현재 실행 중인 작업이 끝나는 것을 기다리지 않고 다른 작업을 할 수 있다.
- 서버, 클라이언트 등 모든 환경에서 유용하게 사용된다.
- UI 애플리케이션의 경우 특정 이벤트가 발생할 경우에 반응하는 동작을 구현해야 하는데 이럴 때 필수적으로 비동기 프로그래밍을 사용하게 된다.

```javascript
// 버튼을 누를때마다 카운터가 증가하는 예제
const button = document.querySelector('button')

// 'click' 옆의 두번째 인자가 비동기 콜백
button.addEventListener('click', event => {
    button.innerHTML = `클릭 수: ${event.detail}`
})
```

- 대부분의 프로그래밍 언어들은 각 언어의 철학에 맞는 다양한 비동기 처리 방법들을 지원한다.
- 대표적으로 `callback`, `promise`, `future`, `async-await`, `coroutine` 등이 있고 각각의 방법들은 장점과 단점이 존재

## 비동기 프로그래밍 구현

### 2.1 Thread

- 가장 기본이 되는 비동기 처리 방식
- 스레드는 `Runnable` 인터페이스를 사용해 비동기 동작을 수행
- 스레드가 1개인 경우 싱글 스레드(Single Thread)라고 부르고 하나 이상 존재하는 경우 멀티 스레드(Multi Thread)라고 한다.

```kotlin
fun main() {
    for (i in 0..5) {
        val thread = Thread {
            println("current-thread-name: ${Thread.currentThread().name}")
        }
        thread.start()
    }
}
```

- 멀티 스레드를 사용하면 스케줄링 알고리즘에 의해 스레드가 전환되면서 작업을 처리하는데 이를 `컨텍스트 스위칭`이라 한다.
- 하나의 `프로세스(Process)`에는 최소한 하나 이상의 스레드가 존재하고 프로세스 내의 스레드들은 동일한 메모리를 공유한다.
- 스레드는 프로세스를 생성하는 것 보다 가볍다.
- 하지만, 스레드가 무한정 많아지면 메모리 사용량이 높아져서 `OOME(OutOfMemoryError)`가 발생할 수 있고 높은 동시 처리량을 요구하는 시스템에서는 스레드를 생성하면서 발생하는 대기 시간 때문에
  응답 지연이 발생한다.
- 이런 문제를 해결하기 위해선 `스레드 풀(Thread Pool)`을 사용해야한다. 스레드 풀을 사용하면 애플리케이션 내에서 사용할 총 스레드 수를 제한할 수 있고 기존에 생성된 스레드를 재사용하므로 빠른 응답이
  가능하다.
- 직접 만드는 것보다 검증된 라이브러리를 사용해야한다. `java.util.concurrent` 패키지의 `ExecutorService`를 사용하면 쉽고 안전하게 스레드 풀을 사용할 수 있다.

```kotlin
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun main() {
    val pool: ExecutorService = Executors.newFixedThreadPool(5)

    try {
        for (i in 0..5) {
            pool.execute {
                println("current-thread-name: ${Thread.currentThread().name}")
            }
        }
    } finally {
        pool.shutdown()
    }
    println("current-thread-name: ${Thread.currentThread().name}")
}
```

### 2.2 Future

- `퓨처(Future)`는 비동기 작업에 대한 결과를 얻고 싶은 경우에 사용된다.
- 예를 들어 수행 시간이 오래 걸리는 작업이나 작업에 대한 결과를 기다리면서 다른 작업을 병행해서 수행하고 싶은 경우에 유용함
- 스레드는 Runnable을 사용해 비동기 처리를 하지만 퓨처를 사용해 처리 결과를 얻기 위해선 `Callable`을 사용한다.

```kotlin
import java.util.concurrent.Callable
import java.util.concurrent.Executors

fun sum(a: Int, b: Int) = a + b

fun main() {
    val pool = Executors.newSingleThreadExecutor()

    val future = pool.submit(Callable {
        sum(100, 200)
    })

    println("계산 시작")
    val futureResult = future.get() // 블로킹 동작
    println(futureResult)
    println("계산 종료")
}
```

- `Future`을 사용하면 비동기 작업을 쉽게 구현할 수 있지만 몇 가지 단점을 가진다.
- 먼저 `get` 함수는 비동기 작업의 처리가 완료될 때까지 다음 코드로 넘어가지 않고 무한정 대기하거나 지정해둔 타임아웃 시간까지 블로킹됨
- 또한 퓨처를 사용하면 동시에 실행되는 한 개의 이상의 비동기 작업에 대한 결과를 하나로 조합하여 처리하거나 수동으로 `완료 처리(completion)` 할 수 있는 방법을 지원하지 않음
- 이러한 단점을 보완하기 위한 API로 `CompletableFuture`이 사용된다.

```kotlin
import java.util.concurrent.CompletableFuture

fun main() {
    val completableFuture = CompletableFuture.supplyAsync {
        Thread.sleep(2000)
        sum(100, 200)
    }

    println("계산 시작")
    completableFuture.thenApplyAsync(::println) // 논블로킹 동작
//    val result = completableFuture.get() // 블로킹 동작
//    println(result)

    while (!completableFuture.isDone) {
        Thread.sleep(500)
        println("계산 결과를 집계 중입니다.")
    }

    println("계산 종료")
}
```

# 옵저버 패턴

## 1. 옵저버 패턴

- 옵저버 패턴(Observer Pattern)이란 GoF가 소개한 디자인 패턴중 하나로 관찰 대상이 되는 객체가 변경되면 대상 객체를 관찰하고 있는 `옵저버(Observer)`에게 변경사항을 `통지(Notify)`
  하는 디자인 패턴입니다.
- 옵저버 패턴을 사용하면 객체 간의 상호작용을 쉽게 하고 효과적으로 데이터를 전달할 수 있다.

### 1.1 옵저버 패턴의 구조

- 옵저버 패턴은 관찰 대상은 `서브젝트(Subject)`와 Subject를 간찰하는 `옵저버(Observer)`로 이뤄져 있다.
- 하나의 서브젝트에는 1개 또는 여러 개의 옵저버를 등록할 수 있다.
- 서브젝트의 상태가 변경되면 자신을 관찰하는 옵저버들에게 변경사항을 통지한다.
- 서브젝트로 변경사항을 통지 받은 옵저버는 부가적인 처리를 한다.
- 옵저버 패턴은 서브젝트와 옵저버를 상속하는 `구체화(Concrete)` 클래스가 존재
- 구체화 클래스는 서브젝트와 옵저버에 대한 상세 구현을 한다.

- 서브젝트의 함수
  > | 함수     | 설명                            |
    > |--------|-------------------------------|
  > | add    | 서브젝트의 상태를 관찰할 옵저버를 등록한다.      |
  > | remove | 등록된 옵저버를 삭제한다.                |
  > | notify | 서브젝트의 상태가 변경되면 등록된 옵저버에 통지한다. |

- 옵저버의 함수
  > |함수|설명|
    > |---|---|
  > | update|서브젝트의 notify 내부에서 호출되며 서브젝트의 변경에 따른 부가 기능을 처리|

### 1.2 옵저버 패턴 구현
- JDK 1.0 부터 포함된 Observable 클래스와 Observer 인터페이스를 사용한 간단한 예제 구현
- java Observer 는 deprecated 되었으므로 `java.util.concurrent` 패키지나 `java.beans.PropertyChangeSupport`와 같은 다양한 옵저버 패턴 구현 방식을 사용하는 것이 좋습니다.
```kotlin

import javafx.beans.Observable
import java.util.*

class Coffee(val name: String)

// Subject
class Barista : Observable() {
  private lateinit var coffeeName: String

  fun orderCoffee(name: String) {
    this.coffeeName = name
  }

  fun makeCoffee() {
    setChanged()
    notifyObservers(Coffee(this.coffeeName))
  }
}

// Observer
class Customer(val name: String) : Observer {
  override fun update(o: java.util.Observable?, arg: Any?) {
    val coffee = arg as Coffee
    println("${name}이 ${coffee.name}을 받았습니다.")
  }
}

fun main() {
    val barista = Barista()
  barista.orderCoffee("아이스 아메리카노")
  
  val customer = Customer("고객1")
  barista.addObserver(customer)
  barista.makeCoffee()
}
```
- 옵저버 패턴의 장점
  - 옵저버 패턴을 사용하지 않았다면 고객은 일정 간격으로 커피가 완성됐는지 바리스타에게 확인하는 처리가 있어야 함
  - 간격이 너무 짧으면 변경된 상태를 빠르게 확인할 수 있지만 매번 불필요한 호출이 발생하므로 성능상 문제가 발생할 수 있음
  - 또한 간격이 너무 길면 변경된 상태를 즉시 확인할 수 없으므로 실시간성이 떨어질 수 있음
  - 옵저버 패턴은 관찰자인 옵저버가 서브젝트의 변화를 신경 쓰지 않고 상태 변경의 주체인 서브젝트가 변경사항을 옵저버에게 알려줌으로써 앞서 언급한 문제를 해결할 수 있다.
  - 옵저버 패턴은 데이터를 제공하는 측에서 데이터를 소비하는 측에 통지하는 `푸시 (Push-Based)` 방식이다.

- 옵저버 패턴에서 서브젝트와 옵저버는 관심사에 따라 역할과 책임이 분리되어 있다.
  - 서브젝트는 옵저버가 어떤 작업을 하는지 옵저버의 상태가 어떤지에 대해 관심을 가질 필요가 없고 오직 변경 사항을 통지하는 역할만 수행하고 하나 혹은 다수의 옵저버는 각각 맡은 역할을 스스로 하기 때문에 옵저버가 하는 일이 서브젝트에 영향을 끼지지 않고 옵저버는 단순한 데이터의 소비자로서 존재하게 된다.

# 이터레이터 패턴
## 1. 이터레이터 패턴
- `이터레이터 패턴(Iterator Pattern)`은 데이터의 집합에서 데이터를 순차적으로 꺼내기 위해 만들어진 디자인 패턴을 말함
- 이터레이터 패턴을 사용하면 컬렉션이 변경되더라도 동일한 인터페이스를 사용해 데이터를 꺼내올 수 있기 때문에 변경사항 없이 사용할 수 있다.
- 데이터의 집합이 얼만큼의 크기를 가진지 알 수 없는 경우 이터레이터 패턴을 사용하면 순차적으로 데이터를 꺼내올 수 있다.
- `어그리게잇(Aggregate)`은 요소들의 집합체를 나타낸다.
- 이터레이터는 집합체 내부에 구현된 iterator를 이용해 생성한다.
- 이터레이터를 사용하는 클라이언트는 생성된 이터레이터의 `hasNext` 함수를 사용해 데이터가 존재하는지 검사하고 `next` 함수를 사용해 데이터를 꺼낸다.
