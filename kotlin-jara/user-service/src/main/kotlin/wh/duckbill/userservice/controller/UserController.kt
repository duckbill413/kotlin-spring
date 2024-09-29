package wh.duckbill.userservice.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import wh.duckbill.userservice.model.*
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

    @GetMapping("/me")
    suspend fun get(
        @AuthToken token: String,
    ): MeResponse {
        return MeResponse(userService.getByToken(token))
    }


    @GetMapping("/{userId}/username")
    suspend fun getUsername(@PathVariable userId: Long): Map<String, String> {
        return mapOf(
            "reporter" to userService.get(userId).username
        )
    }
}