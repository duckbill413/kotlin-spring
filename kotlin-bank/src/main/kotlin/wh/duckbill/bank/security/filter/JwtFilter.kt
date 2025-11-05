package wh.duckbill.bank.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import wh.duckbill.bank.common.exception.CustomException
import wh.duckbill.bank.common.exception.ErrorCode
import wh.duckbill.bank.common.jwt.JwtProvider
import wh.duckbill.bank.types.dto.ResponseProvider

private val JWT_AUTH_ENDPOINT = arrayOf(
  "/api/v1/bank/**",
//  "/api/v1/history/**"
)

@Component
class JwtFilter(
  private val jwtProvider: JwtProvider,
) : OncePerRequestFilter() {

  private final val pathMatcher = AntPathMatcher()

  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) {
    val requestUri = request.requestURI

    if (shouldPerformAuthentication(requestUri)) {
      val authHeader = request.getHeader("Authorization")
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        val token = authHeader.substring(7)
        try {
          jwtProvider.verify(token)
        } catch (e: CustomException) {
          response.status = HttpServletResponse.SC_UNAUTHORIZED
          response.contentType = "application/json"
          val msg = e.getCodeInterface()

          val errorResponse = ResponseProvider.failed(
            HttpStatus.UNAUTHORIZED,
            msg.message,
            null
          )
          response.writer.write(ObjectMapper().writeValueAsString(errorResponse))
          response.writer.flush()
          return
        }
      } else {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"

        val errorResponse = ResponseProvider.failed(
          HttpStatus.UNAUTHORIZED,
          ErrorCode.ACCESS_TOKEN_NEED.message,
          null
        )
        response.writer.write(ObjectMapper().writeValueAsString(errorResponse))
        response.writer.flush()
      }
    }

    filterChain.doFilter(request, response)
  }

  private fun shouldPerformAuthentication(uri: String): Boolean {
    for (endPoint in JWT_AUTH_ENDPOINT) {
      pathMatcher.match(endPoint, uri) && return true
    }
    return false
  }
}
