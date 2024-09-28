package wh.duckbill.userservice.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import wh.duckbill.userservice.model.AuthToken
import wh.duckbill.userservice.model.SignInRequest
import wh.duckbill.userservice.model.SignInResponse
import wh.duckbill.userservice.model.SignUpRequest
import wh.duckbill.userservice.service.UserService

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {
    @PostMapping("/signup")
    suspend fun signUp(@RequestBody request: SignUpRequest) =
        userService.signUp(request)

    @PostMapping("/signin")
    suspend fun signIn(@RequestBody request: SignInRequest): SignInResponse =
        userService.signIn(request)

    @DeleteMapping("/signout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun logout(@AuthToken token: String) =
        userService.logout(token)
}