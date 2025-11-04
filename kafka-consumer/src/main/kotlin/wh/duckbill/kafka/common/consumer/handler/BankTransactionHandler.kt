package wh.duckbill.kafka.common.consumer.handler

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import wh.duckbill.kafka.common.cache.RedisClient
import wh.duckbill.kafka.common.consumer.repository.HistoryMongoRepository
import wh.duckbill.kafka.`interface`.Handler

@Component
class BankTransactionHandler(
  private val logger: Logger = LoggerFactory.getLogger(BankTransactionHandler::class.java),
  private val historyMongoRepository: HistoryMongoRepository,
  private val redisClient: RedisClient,
) : Handler {
  override fun handle(
    record: ConsumerRecord<String, Any>,
    acknowledgment: Acknowledgment?
  ) {
    TODO("Not yet implemented")
  }

  override fun handleDLQ(
    record: ConsumerRecord<String, Any>,
    acknowledgment: Acknowledgment?
  ) {
    TODO("Not yet implemented")
  }
}