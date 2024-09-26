package wh.duckbill.userservice.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWT.*
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.algorithms.Algorithm.HMAC256
import com.auth0.jwt.interfaces.DecodedJWT
import wh.duckbill.userservice.config.JWTProperties
import java.util.*

object JWTUtils {
    fun createToken(claim: JWTClaim, properties: JWTProperties) =
        create()
            .withIssuer(properties.issuer)
            .withSubject(properties.subject)
            .withIssuedAt(Date())
            .withExpiresAt(Date(Date().time + properties.expiresTime * 1000))
            .withClaim("userId", claim.userId)
            .withClaim("username", claim.username)
            .withClaim("email", claim.email)
            .withClaim("profileUrl", claim.profileUrl)
            .sign(HMAC256(properties.secret))

    fun decode(token: String, secret: String, issuer: String): DecodedJWT {
        val algorithm = HMAC256(secret)

        val verifier = require(algorithm)
            .withIssuer(issuer)
            .build()

        return verifier.verify(token)
    }
}

data class JWTClaim(
    val userId: Long,
    val email: String,
    val profileUrl: String,
    val username: String,
)