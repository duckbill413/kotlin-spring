package wh.duckbill.userservice.service

import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.stereotype.Service
import wh.duckbill.userservice.config.JWTProperties
import wh.duckbill.userservice.domain.entity.User
import wh.duckbill.userservice.domain.repository.UserRepository
import wh.duckbill.userservice.exception.InvalidJwtTokenException
import wh.duckbill.userservice.exception.PasswordNotMatchedException
import wh.duckbill.userservice.exception.UserExistsException
import wh.duckbill.userservice.exception.UserNotFoundException
import wh.duckbill.userservice.model.SignInRequest
import wh.duckbill.userservice.model.SignInResponse
import wh.duckbill.userservice.model.SignUpRequest
import wh.duckbill.userservice.model.UserEditRequest
import wh.duckbill.userservice.utils.BCryptUtils
import wh.duckbill.userservice.utils.JWTClaim
import wh.duckbill.userservice.utils.JWTUtils
import java.time.Duration

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtProperties: JWTProperties,
    private val cacheManager: CoroutineCacheManager<User>,
) {
    companion object {
        private val CACHE_TTL = Duration.ofMinutes(1)
    }

    suspend fun signUp(signUpRequest: SignUpRequest) {
        with(signUpRequest) {
            userRepository.findByEmail(email)?.let {
                throw UserExistsException()
            }

            val user = User(
                email = email,
                password = BCryptUtils.hash(password),
                username = username,
            )

            userRepository.save(user)
        }
    }

    suspend fun signIn(request: SignInRequest): SignInResponse {
        return with(userRepository.findByEmail(request.email) ?: throw UserNotFoundException()) {
            val verified = BCryptUtils.verify(request.password, password)
            if (!verified) {
                throw PasswordNotMatchedException()
            }

            val jwtClaim = JWTClaim(
                userId = id!!,
                email = email,
                profileUrl = profileUrl,
                username = username
            )

            val token = JWTUtils.createToken(jwtClaim, jwtProperties)
            // token 을 저장할 필요가 있음
            // 현재는 캐시 메니저를 사용
            cacheManager.awaitPut(
                key = token, value = this, ttl = CACHE_TTL
            )

            SignInResponse(
                email = email,
                username = username,
                token = token,
            )
        }
    }

    suspend fun logout(token: String) {
        cacheManager.awaitEvict(token)
    }

    suspend fun getByToken(token: String): User {
        val cachedUser = cacheManager.awaitGetOrPut(key = token, ttl = CACHE_TTL) {
            // 캐시가 유효하지 않은 경우 동작
            val decodedJWT: DecodedJWT = JWTUtils.decode(token, jwtProperties.secret, jwtProperties.issuer)

            val userId: Long = decodedJWT.claims["userId"]?.asLong() ?: throw InvalidJwtTokenException()
            get(userId)
        }
        return cachedUser
    }

    suspend fun get(userId: Long) : User {
        return userRepository.findById(userId) ?: throw UserNotFoundException()
    }

    suspend fun edit(token: String, request: UserEditRequest, profileUrl: String?): User {
        val user = getByToken(token)

        val newUser = user.copy(
            username = request.username,
            profileUrl = profileUrl ?: user.profileUrl,
        )

        return userRepository.save(newUser).also {
            cacheManager.awaitPut(
                key = token,
                value = newUser,
                ttl = CACHE_TTL
            )
        }
    }
}