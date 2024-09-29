package wh.duckbill.userservice.controller

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import wh.duckbill.userservice.model.*
import wh.duckbill.userservice.service.UserService
import java.io.File

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

    @PostMapping("/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun edit(
        @PathVariable id: Long,
        @ModelAttribute request: UserEditRequest,
        @AuthToken token: String,
        @RequestPart("profileUrl") filePart: FilePart,
    ) {
        val orgFilename = filePart.filename()
        var filename: String? = null

        if (orgFilename.isNotBlank()) {
            val ext = orgFilename.substring(orgFilename.lastIndexOf(".") + 1) // 확장자
            filename = "${id}.${ext}"

            val file = File(ClassPathResource("/images").file, filename)
            filePart.transferTo(file).awaitSingleOrNull()
        }

        userService.edit(token, request, filename)
    }
}