package wh.duckbill.bank.common.cache

import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.RedisTemplate
import wh.duckbill.bank.common.exception.CustomException
import wh.duckbill.bank.common.exception.ErrorCode
import java.util.concurrent.TimeUnit

class RedisClient(
  private val template: RedisTemplate<String, String>,
  private val redissonClient: RedissonClient
) {

  fun get(key: String): String? = template.opsForValue().get(key)

  fun <T> get(key: String, kSerializer: (Any) -> T?): T? {
    val value = template.opsForValue().get(key)
    return value?.let { kSerializer(it) }
  }

  fun setIfNotExist(key: String, value: String): Boolean = template.opsForValue().setIfAbsent(key, value) ?: false

  fun <T> invokeWithMutex(key: String, function: () -> T?) {
    val lock = redissonClient.getLock(key)

    try {
      lock.lock(15, TimeUnit.SECONDS)
      function.invoke()
    } catch (e: Exception) {
      throw CustomException(ErrorCode.FAILED_TO_MUTEX_INVOKE)
    } finally {
      lock.unlock()
    }
  }
}