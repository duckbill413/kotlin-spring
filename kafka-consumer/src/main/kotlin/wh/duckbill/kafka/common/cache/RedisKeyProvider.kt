package wh.duckbill.kafka.common.cache

object RedisKeyProvider {
  private const val HIST_CACHE_KEY = "history"

  fun historyCacheKey(ulid: String) = "${HIST_CACHE_KEY}:$ulid"
}