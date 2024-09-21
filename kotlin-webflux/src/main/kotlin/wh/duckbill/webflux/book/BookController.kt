package wh.duckbill.webflux.book

import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class BookController(
    private val bookService: BookService
) {
    /**
     * Controller 함수의 type 이 Mono, Flux 라면
     * Webflux 에서 subscribe 를 자동으로 호출해 줌
     */
    @GetMapping("/books")
    fun getAll(): Flux<Book> {
        return bookService.getAll()
    }

    @GetMapping("/books/{id}")
    fun get(@PathVariable id: Int): Mono<Book> =
        bookService.get(id)

    @PostMapping("/books")
    fun add(@RequestBody request: Map<String, Any>): Mono<Book> =
        bookService.add(request)

    @DeleteMapping("/books/{id}")
    fun delete(@PathVariable id: Int): Mono<Void> = bookService.delete(id)

}