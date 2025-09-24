package wh.duckbill.bank.common.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.AlgorithmMismatchException
import com.auth0.jwt.exceptions.InvalidClaimException
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import wh.duckbill.bank.common.exception.CustomException
import wh.duckbill.bank.common.exception.ErrorCode
import java.util.*

@Component
class JwtProvider(
  @Value("\${jwt.secret-key}")
  private val secretKey: String,
  @Value("\${jwt.time")
  private val time: Long,
) {

  private val ONE_MINUTE_TO_MILLIS: Long = 60 * 60 * 1000

  fun createToken(platform: String, email: String?, name: String?, id: String): String {
    return JWT.create()
      .withSubject("$platform - $email - $name - $id")
      .withIssuedAt(Date())
      .withExpiresAt(Date(System.currentTimeMillis() + time * ONE_MINUTE_TO_MILLIS))
      .sign(Algorithm.HMAC256(secretKey))
  }

  /**
   * `AlgorithmMismatchException` – if the algorithm stated in the token's header it's not equal to the one defined in the JWTVerifier.
   * `SignatureVerificationException` – if the signature is invalid.
   * `TokenExpiredException` – if the token has expired.
   * `InvalidClaimException` – if a claim contained a different value than the expected one.
   */
  fun verify(token: String): DecodedJWT {
    try {
      return JWT.require(Algorithm.HMAC256(secretKey))
        .build()
        .verify(token)
    } catch (e: AlgorithmMismatchException) {
      throw CustomException(ErrorCode.TOKEN_IS_INVALID, e.message)
    } catch (e: SignatureVerificationException) {
      throw CustomException(ErrorCode.TOKEN_IS_INVALID, e.message)
    } catch (e: TokenExpiredException) {
      throw CustomException(ErrorCode.TOKEN_IS_EXPIRED, e.message)
    } catch (e: InvalidClaimException) {
      throw CustomException(ErrorCode.TOKEN_IS_INVALID, e.message)
    }
  }
}