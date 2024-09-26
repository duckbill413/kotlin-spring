package wh.duckbill.userservice.utils

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import wh.duckbill.userservice.config.JWTProperties

class JWTUtilsTest {
    private val logger = KotlinLogging.logger {}

    @Test
    fun createTokenTest() {
        val jwtClaim = JWTClaim(
            userId = 1,
            email = "duckbill@gmail.com",
            profileUrl = "duckbill.jpg",
            username = "duckbill",
        )

        val properties = JWTProperties(
            issuer = "jara",
            subject = "auth",
            expiresTime = 3600,
            secret = "my-secret"
        )

        val token = JWTUtils.createToken(jwtClaim, properties)
        assertNotNull(token)

        logger.info { "token: $token" }
    }

    @Test
    fun decodeTest() {
        val jwtClaim = JWTClaim(
            userId = 1,
            email = "duckbill@gmail.com",
            profileUrl = "duckbill.jpg",
            username = "duckbill",
        )

        val properties = JWTProperties(
            issuer = "jara",
            subject = "auth",
            expiresTime = 3600,
            secret = "my-secret"
        )

        val token = JWTUtils.createToken(jwtClaim, properties)

        val decode = JWTUtils.decode(token, secret = properties.secret, issuer = properties.issuer)

        with(decode) {
            logger.info { "claim: $claims" }

            val userId = claims["userId"]!!.asLong()
            assertEquals(userId, jwtClaim.userId)
        }
    }
}