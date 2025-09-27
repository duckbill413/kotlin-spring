package wh.duckbill.bank.domains.auth.controller

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import wh.duckbill.bank.domains.auth.service.AuthService
import java.net.URI

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
  private val authService: AuthService
) {

  @GetMapping("/callback")
  fun callback(
    @RequestParam("code") code: String,
    @RequestParam("state") state: String,
    response: HttpServletResponse
  ): ResponseEntity<String> {
    val token = authService.handleAuth(state, code)

    response.addCookie(
      Cookie("authToken", token).apply {
        isHttpOnly = true
        path = "/"
        maxAge = 60 * 60 * 24
      }
    )

    return ResponseEntity.status(HttpStatus.FOUND)
      .location(URI.create("http://localhost:3000"))
      .build()
  }

  @GetMapping("verify-token")
  fun verifyToken(@RequestHeader("Authorization") authHeader: String) {
    authService.verifyToken(authHeader)
  }

}