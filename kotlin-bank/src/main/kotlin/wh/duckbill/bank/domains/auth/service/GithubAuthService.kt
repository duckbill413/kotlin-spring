package wh.duckbill.bank.domains.auth.service

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.FormBody
import org.springframework.stereotype.Service
import wh.duckbill.bank.common.exception.CustomException
import wh.duckbill.bank.common.exception.ErrorCode
import wh.duckbill.bank.common.httpClient.CallClient
import wh.duckbill.bank.common.json.JsonUtil
import wh.duckbill.bank.config.OAuth2Config
import wh.duckbill.bank.interfaces.OAuth2TokenResponse
import wh.duckbill.bank.interfaces.OAuth2UserResponse
import wh.duckbill.bank.interfaces.OAuthServiceInterface

private const val key = "github"

@Service(key)
class GithubAuthService(
  private val httpClient: CallClient,
  private val config: OAuth2Config
) : OAuthServiceInterface {
  private val oAuthInfo = config.providers[key] ?: throw CustomException(ErrorCode.AUTH_CONFIG_NOT_FOUND, key)
  private val tokenURL = "https://github.com/login/oauth/access_token"
  private val userInfoURL = "https://api.github.com/user"

  override val providerName: String = key

  override fun getToken(code: String): OAuth2TokenResponse {
    val body = FormBody.Builder()
      .add("code", code)
      .add("client_id", oAuthInfo.clientId)
      .add("client_secret", oAuthInfo.clientSecret)
      .add("redirect_uri", oAuthInfo.redirectUri)
      .add("grant_type", "authorization_code")
      .build()

    val headers = mapOf("Accept" to "application/json")
    val jsonString = httpClient.POST("", headers, body)
    val response = JsonUtil.decodeFromJson<GithubTokenResponse>(jsonString, GithubTokenResponse.serializer())
    return response
  }

  override fun getUser(token: String): OAuth2UserResponse {
    val headers = mapOf(
      "Content-Type" to "application/json",
      "Authorization" to "Bearer $token"
    )

    val jsonString = httpClient.GET(userInfoURL, headers)
    val response: GithubUserResponseTemp = JsonUtil.decodeFromJson(jsonString, GithubUserResponseTemp.serializer())
    return response.toOAuth2UserResponse()
  }
}

@Serializable
data class GithubTokenResponse(
  @SerialName("access_token")
  override val accessToken: String,
) : OAuth2TokenResponse

@Serializable
data class GithubUserResponseTemp(
  val id: Int,
  val repos_url: String,
  val name: String,
  val email: String?,
) {
  fun toOAuth2UserResponse(): GithubUserResponse {
    return GithubUserResponse(
      id = id.toString(),
      email = repos_url,
      name = name,
    )
  }
}

@Serializable
data class GithubUserResponse(
  override val id: String,
  override val email: String?,
  override val name: String?,
) : OAuth2UserResponse