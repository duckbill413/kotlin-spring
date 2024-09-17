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
- 하지만, 스레드가 무한정 많아지면 메모리 사용량이 높아져서 `OOME(OutOfMemoryError)`가 발생할 수 있고 높은 동시 처리량을 요구하는 시스템에서는 스레드를 생성하면서 발생하는 대기 시간 때문에 응답 지연이 발생한다.
- 이런 문제를 해결하기 위해선 `스레드 풀(Thread Pool)`을 사용해야한다. 스레드 풀을 사용하면 애플리케이션 내에서 사용할 총 스레드 수를 제한할 수 있고 기존에 생성된 스레드를 재사용하므로 빠른 응답이 가능하다.
- 직접 만드는 것보다 검증된 라이브러리를 사용해야한다. `java.util.concurrent` 패키지의 `ExecutorService`를 사용하면 쉽고 안전하게 스레드 풀을 사용할 수 있다.
```kotlin
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun main() {
    val pool: ExecutorService = Executors.newFixedThreadPool(5)
    
    try {
        for (i in 0..5) {
            pool.execute{
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