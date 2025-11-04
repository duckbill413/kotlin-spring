package wh.duckbill.kafka.common.consumer.repository

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository
import wh.duckbill.kafka.common.exception.CustomException
import wh.duckbill.kafka.common.exception.ErrorCode
import wh.duckbill.kafka.config.MongoTableCollector
import wh.duckbill.kafka.types.entity.TransactionHistoryDocument

@Repository
class HistoryMongoRepository(
  @Qualifier("template")
  private val mongoTemplate: HashMap<String, MongoTemplate>,
) {

  fun saveTransactionHistory(event: TransactionHistoryDocument) {
    val template = template(MongoTableCollector.Bank)
    template.save(event)
  }

  private fun template(mongoTableCollector: MongoTableCollector) =
    mongoTemplate[mongoTableCollector.table] ?: throw CustomException(ErrorCode.FAILED_TO_FIND_MONGO_TEMPLATE)
}