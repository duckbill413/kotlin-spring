package wh.duckbill.bank.domains.auth.service

import org.springframework.stereotype.Service
import wh.duckbill.bank.interfaces.OAuthServiceInterface

@Service
class AuthService(
  private val oAuthServices: Map<String, OAuthServiceInterface>
) {
}