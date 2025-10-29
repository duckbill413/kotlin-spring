package wh.duckbill.bank.security.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import wh.duckbill.bank.security.filter.JwtFilter

@Configuration
@EnableWebSecurity
class WebSecurity(
  private val jwtFilter: JwtFilter,
) {

  @Bean
  fun jwtSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http
      .csrf { it.disable() }
      .formLogin { it.disable() }
      .httpBasic { it.disable() } // 헤더에 사용자 정보 표시
      .cors { it.configurationSource(corsConfigurationSource()) }
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

    return http.build()
  }
  
  @Bean
  fun corsConfigurationSource(): CorsConfigurationSource {
    val config = CorsConfiguration()
    config.allowedOrigins = listOf("*")
    config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
    config.allowedHeaders = listOf("*")

    val source = UrlBasedCorsConfigurationSource()
    source.registerCorsConfiguration("/**", config)
    return source
  }
}