package wh.duckbill.bank.domains.auth.service

import org.springframework.stereotype.Service
import wh.duckbill.bank.common.exception.CustomException
import wh.duckbill.bank.common.exception.ErrorCode
import wh.duckbill.bank.common.jwt.JwtProvider
import wh.duckbill.bank.interfaces.OAuthServiceInterface

@Service
class AuthService(
  private val oAuthServices: Map<String, OAuthServiceInterface>,
  private val jwtProvider: JwtProvider
) {
  fun handleAuth(state: String, code: String): String {

    // GOOGLE -> google
    val provider = state.lowercase()
    val callService = oAuthServices[provider] ?: throw CustomException(ErrorCode.PROVIDER_NOT_FOUND)

    val accessToken = callService.getToken(code)
    val userInfo = callService.getUser(accessToken.accessToken)
    val token = jwtProvider.createToken(
      platform = provider,
      email = userInfo.email,
      name = userInfo.name,
      id = userInfo.id
    )
    // userInfo
    
  }
}