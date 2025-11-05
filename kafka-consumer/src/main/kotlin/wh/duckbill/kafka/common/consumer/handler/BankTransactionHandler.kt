package wh.duckbill.kafka.common.consumer.handler

import kotlinx.serialization.builtins.ListSerializer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import wh.duckbill.kafka.common.cache.RedisClient
import wh.duckbill.kafka.common.cache.RedisKeyProvider
import wh.duckbill.kafka.common.consumer.repository.HistoryMongoRepository
import wh.duckbill.kafka.common.json.JsonUtil
import wh.duckbill.kafka.`interface`.Handler
import wh.duckbill.kafka.types.dto.History
import wh.duckbill.kafka.types.dto.toHistory
import wh.duckbill.kafka.types.message.TransactionMessage

@Component
class BankTransactionHandler(
  private val logger: Logger = LoggerFactory.getLogger(BankTransactionHandler::class.java),
  private val historyMongoRepository: HistoryMongoRepository,
  private val redisClient: RedisClient,
) : Handler {
  private final val MAX_CACHED_SIZE: Int = 10

  override fun handle(
    record: ConsumerRecord<String, Any>,
    acknowledgment: Acknowledgment?
  ) {
    acknowledgment?.let {
      val rawMessage = record.value()
      logger.info("Received message: {}", rawMessage)

      try {
        val messageString = when (rawMessage) {
          is String -> rawMessage
          else -> rawMessage.toString()
        }

        val data = JsonUtil.decodeFromJson(messageString, TransactionMessage.serializer())
        val entity = data.toEntity()
        historyMongoRepository.saveTransactionHistory(entity)
        handlerRedisData(entity.fromUlid, data.toHistory())
        handlerRedisData(entity.toUlid, data.toHistory())

        it.acknowledge()
        logger.info("Message processed successfully: {}", rawMessage)
      } catch (e: Exception) {
        logger.error("Message processing failed message: {}", rawMessage, e)
      }
    }
  }

  override fun handleDLQ(
    record: ConsumerRecord<String, Any>,
    acknowledgment: Acknowledgment?
  ) {
    TODO("Not yet implemented")
  }

  fun handlerRedisData(ulid: String, dto: History) {
    val cachedKey = RedisKeyProvider.historyCacheKey(ulid)
    val cachedValue = redisClient.get(cachedKey)
    if (cachedValue != null) {
      val cachedData: List<History> = JsonUtil.decodeFromJson(cachedValue, ListSerializer(History.serializer()))
      val updatedHistoryList = if (cachedData.size >= MAX_CACHED_SIZE) {
        val mutableList = cachedData.toMutableList()
        mutableList.removeAt(mutableList.size - 1)
        mutableList.add(dto)
        mutableList
      } else {
        val mutableList = cachedData.toMutableList()
        mutableList.add(dto)
        mutableList
      }

      redisClient.set(cachedKey, JsonUtil.encodeToJson(updatedHistoryList, ListSerializer(History.serializer())))
    }
  }
}