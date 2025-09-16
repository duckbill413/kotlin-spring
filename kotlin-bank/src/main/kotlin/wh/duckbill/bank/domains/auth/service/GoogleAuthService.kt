package wh.duckbill.bank.domains.auth.service

import org.springframework.stereotype.Service
import wh.duckbill.bank.common.exception.CustomException
import wh.duckbill.bank.common.exception.ErrorCode
import wh.duckbill.bank.config.OAuth2Config
import wh.duckbill.bank.interfaces.OAuth2TokenResponse
import wh.duckbill.bank.interfaces.OAuth2UserResponse
import wh.duckbill.bank.interfaces.OAuthServiceInterface

private const val key = "google"

@Service(key)
class GoogleAuthService(
  private val config: OAuth2Config
) : OAuthServiceInterface {
  private val oAuthInfo = config.providers[key] ?: throw CustomException(ErrorCode.AUTH_CONFIG_NOT_FOUND)

  override val providerName: String = key

  override fun getToken(code: String): OAuth2TokenResponse {
    TODO("Not yet implemented")
  }

  override fun getUser(token: String): OAuth2UserResponse {
    TODO("Not yet implemented")
  }
}