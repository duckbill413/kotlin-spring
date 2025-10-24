package wh.duckbill.bank.domains.history.repository

import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class HistoryMongoRepository(
  private val mongoTemplate: HashMap<String, MongoTemplate>,
  private val historyUserRepository: HistoryUserRepository,
) {

  fun findLatestTransactionHistory(ulid: String, limit: Int = 30) {
    val criteria = Criteria().orOperator(
      Criteria.where("fromUlid").`is`(ulid),
      Criteria.where("toUlid").`is`(ulid),
    )

    val query = Query(criteria)
      .with(Sort.by(Sort.Direction.ASC, "time"))
      .limit(limit)

    query.fields().exclude("_id")

  }
}