package wh.duckbill.kafka.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration


@Configuration
class RedisConfig {

  @Bean
  fun redisConnectionFactory(
    @Value("\${database.redis.host}") host: String,
    @Value("\${database.redis.port}") port: Int,
    @Value("\${database.redis.password}") password: String?,
    @Value("\${database.redis.database:${0}}") database: Int,
    @Value("\${database.redis.timeout:${10000}}") timeout: Long,
  ): LettuceConnectionFactory {
    val config = RedisStandaloneConfiguration(host, port).apply {
      password?.let { setPassword(it) }
      setDatabase(database)
    }

    val clientConfig = LettuceClientConfiguration.builder()
      .commandTimeout(Duration.ofSeconds(timeout))
      .build()

    return LettuceConnectionFactory(config, clientConfig)
  }

  @Bean
  @Primary
  fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> {
    val template = RedisTemplate<String, String>()
    return template.apply {
      setConnectionFactory(connectionFactory)
      template.keySerializer = StringRedisSerializer()
      template.valueSerializer = Jackson2JsonRedisSerializer(String::class.java)
      template.hashKeySerializer = StringRedisSerializer()
      template.hashValueSerializer = Jackson2JsonRedisSerializer(String::class.java)
      template.afterPropertiesSet()
    }
  }

  @Bean
  fun redissonClient(
    @Value("\${database.redisson.host}") host: String,
    @Value("\${database.redisson.timeout}") timeout: Int,
    @Value("\${database.redisson.password}") password: String?,
  ): RedissonClient {
    val config = Config().apply {
      useSingleServer()
        .setAddress(host)
        .setPassword(password)
    }

    return Redisson.create(config).also {
      println("redisson create success")
    }
  }
}