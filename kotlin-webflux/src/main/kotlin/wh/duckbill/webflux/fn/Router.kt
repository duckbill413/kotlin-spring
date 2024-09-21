package wh.duckbill.webflux.fn

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class Router {

    @Bean
    fun helloRouter(handler: HelloHandler): RouterFunction<ServerResponse> =
        route()
            .GET("/", handler::sayHello)
            .build()

    @Bean
    fun userRouter(handler: UserHandler): RouterFunction<ServerResponse> =
//        route()
//            .GET("/users/{id}", handler::getUser)
//            .GET("/users", handler::getAll)
//            .build()
        // 중첩라이팅으로 변경
        router {
            "/users".nest {
                GET("/{id}", handler::getUser)
                GET("", handler::getAll)
            }
        }
}