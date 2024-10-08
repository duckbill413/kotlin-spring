package wh.duckbill.webflux.fn

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class HelloHandler {
    fun sayHello(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok()
            .bodyValue("Hello WebFlux")
    }
}
