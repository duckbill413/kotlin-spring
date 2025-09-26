package wh.duckbill.bank.domains.auth.service

import com.github.f4b6a3.ulid.UlidCreator
import org.slf4j.Logger
import org.springframework.stereotype.Service
import wh.duckbill.bank.common.exception.CustomException
import wh.duckbill.bank.common.exception.ErrorCode
import wh.duckbill.bank.common.jwt.JwtProvider
import wh.duckbill.bank.common.logging.Logging
import wh.duckbill.bank.common.transaction.Transactional
import wh.duckbill.bank.domains.auth.repository.AuthUserRepository
import wh.duckbill.bank.interfaces.OAuthServiceInterface
import wh.duckbill.bank.types.entity.User

@Service
class AuthService(
  private val oAuthServices: Map<String, OAuthServiceInterface>,
  private val jwtProvider: JwtProvider,
  private val logger: Logger = Logging.getLogger(AuthService::class.java),
  private val transactional: Transactional,
  private val authUserRepository: AuthUserRepository
) {

  fun handleAuth(state: String, code: String): String? = Logging.logFor(logger) { log ->
    // GOOGLE -> google
    val provider = state.lowercase()
    log["provider"] = provider

    val callService = oAuthServices[provider] ?: throw CustomException(ErrorCode.PROVIDER_NOT_FOUND)

    val accessToken = callService.getToken(code)
    val userInfo = callService.getUser(accessToken.accessToken)
    val token = jwtProvider.createToken(
      platform = provider,
      email = userInfo.email,
      name = userInfo.name,
      id = userInfo.id
    )
    val username = (userInfo.name ?: userInfo.email).toString()

    transactional.run {
      val exist = authUserRepository.existsByUsername(username)

      if (exist) {
        // accessToken create
      } else {
        val ulid = UlidCreator.getUlid().toString()
        val user = User(ulid, username, token)
        authUserRepository.save(user)
      }
    }
    // userInfo
    return@logFor null
  }

  fun verifyToken(token: String) {
    jwtProvider.verify(token.removePrefix("Bearer "))
  }
}