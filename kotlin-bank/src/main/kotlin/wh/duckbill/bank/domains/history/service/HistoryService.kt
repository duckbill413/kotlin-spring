package wh.duckbill.bank.domains.history.service

import kotlinx.serialization.builtins.ListSerializer
import org.slf4j.Logger
import org.springframework.stereotype.Service
import wh.duckbill.bank.common.cache.RedisClient
import wh.duckbill.bank.common.cache.RedisKeyProvider
import wh.duckbill.bank.common.json.JsonUtil
import wh.duckbill.bank.common.logging.Logging
import wh.duckbill.bank.domains.history.repository.HistoryMongoRepository
import wh.duckbill.bank.types.dto.History
import wh.duckbill.bank.types.dto.Response
import wh.duckbill.bank.types.dto.ResponseProvider

@Service
class HistoryService(
  private val historyMongoRepository: HistoryMongoRepository,
  private val redisClient: RedisClient,
  private val logger: Logger = Logging.getLogger(HistoryService::class.java)
) {

  fun history(ulid: String): Response<List<History>> = Logging.logFor(logger) {
    it["ulid"] = ulid
    val key = RedisKeyProvider.historyCacheKey(ulid)
    val cached = redisClient.get(key)
    return@logFor when (cached) {
      null -> {
        val result = historyMongoRepository.findLatestTransactionHistory(ulid)
        redisClient.setIfNotExist(key, JsonUtil.encodeToJson(result, ListSerializer(History.serializer())))
        ResponseProvider.success(result)
      }

      else -> {
        val cachedData = JsonUtil.decodeFromJson(cached, ListSerializer(History.serializer()))
        ResponseProvider.success(cachedData)
      }
    }
  }
}