package wh.duckbill.bank.domains.history.repository

import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import wh.duckbill.bank.common.exception.CustomException
import wh.duckbill.bank.common.exception.ErrorCode
import wh.duckbill.bank.config.MongoTableCollector
import wh.duckbill.bank.types.dto.History
import wh.duckbill.bank.types.dto.toHistory
import wh.duckbill.bank.types.entity.TransactionHistoryDocument
import java.util.concurrent.ConcurrentHashMap

@Repository
class HistoryMongoRepository(
  private val mongoTemplate: HashMap<String, MongoTemplate>,
  private val historyUserRepository: HistoryUserRepository,
  private val userNameMapper: ConcurrentHashMap<String, String> = ConcurrentHashMap()
) {

  fun findLatestTransactionHistory(ulid: String, limit: Int = 30): List<History> {
    val criteria = Criteria().orOperator(
      Criteria.where("fromUlid").`is`(ulid),
      Criteria.where("toUlid").`is`(ulid),
    )

    val query = Query(criteria)
      .with(Sort.by(Sort.Direction.ASC, "time"))
      .limit(limit)

    query.fields().exclude("_id")

    val result: List<TransactionHistoryDocument> =
      getTemplate(MongoTableCollector.Bank).find(query, TransactionHistoryDocument::class.java)

    return result.map { doc ->
      val fromName = getUserName(doc.fromUlid)
      val toName = getUserName(doc.toUlid)
      doc.toHistory(fromName, toName)
    }
  }

  private fun getUserName(ulid: String): String {
    val value = userNameMapper[ulid] ?: ""
    if (value.isEmpty()) {
      val user = historyUserRepository.findByUlid(ulid)
      userNameMapper[ulid] = user.username
      return user.username
    }
    return value
  }

  private fun getTemplate(collector: MongoTableCollector): MongoTemplate =
    mongoTemplate[collector.table] ?: throw CustomException(ErrorCode.FAILED_TO_FIND_MONGO_TEMPLATE)
}