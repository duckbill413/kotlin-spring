package wh.duckbill.webflux.webclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import wh.duckbill.webflux.book.Book

@RestController
class WebClientExchanger {
    val url: String = "http://localhost:8080"
    val log: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * WebClient
     *
     * Reactive 기반의 Non-Blocking Http Client
     * Spring 5 부터는 RestTemplate 를 대체하여 WebClient가 사용됨
     * Non-Blocking, Blocking 모두 지원
     *
     * 아래 예제의 로그를 확인해 보자
     * 결과값이 도착하기 전에 'End WebClient' 가 출력되는 것을 확인할 수 있다.
     * WebClient 가 Non-Blocking 으로 동작하기 때문
     * Spring-MVC 를 사용하더라도 webflux 의존성을 추가하여 쉽게 사용할 수 있다.
     */
    @GetMapping("/books/nonblock")
    fun getBooksNonBlockingWay(): Flux<Book> {
        log.info("Start WebClient")
        val flux = WebClient.create(url)
            .get()
            .uri("/books")
            .retrieve()
            .bodyToFlux(Book::class.java)
            .map {
                log.info("result: {}", it)
                it
            }
        log.info("End WebClient")
        return flux
    }
}