package wh.duckbill.issue.exception

import com.auth0.jwt.exceptions.TokenExpiredException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = KotlinLogging.logger {};

    @ExceptionHandler(ServerException::class)
    fun serverExceptionHandler(ex: ServerException): ErrorResponse {
        logger.error { ex.message } // 에러가 발생시 logger lambda 로 message 출력
        return ErrorResponse(
            code = ex.code,
            message = ex.message
        )
    }

    @ExceptionHandler(TokenExpiredException::class)
    fun tokenExpiredExceptionHandler(ex: TokenExpiredException): ErrorResponse {
        logger.error { ex.message } // 에러가 발생시 logger lambda 로 message 출력
        return ErrorResponse(
            code = 401,
            message = "Token Expired Error"
        )
    }

    @ExceptionHandler(Exception::class)
    fun exceptionHandler(ex: Exception): ErrorResponse {
        logger.error { ex.message }
        return ErrorResponse(
            code = 401,
            message = "Internal Server Error"
        )
    }
}