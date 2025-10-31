package wh.duckbill.kafka.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ReadPreference
import com.mongodb.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.core.MongoTemplate
import wh.duckbill.kafka.common.exception.CustomException
import wh.duckbill.kafka.common.exception.ErrorCode
import kotlin.collections.set

enum class MongoTableCollector(
  val table: String,
) {
  Bank("bank")
}

@Configuration
@EnableMongoAuditing
class MongoConfig(
  @Value("\${database.mongo.uri}") val uri: String,
) {

  @Bean
  fun template(): HashMap<String, MongoTemplate> {
    val mapper = HashMap<String, MongoTemplate>(MongoTableCollector.entries.size)
    val settings = MongoClientSettings.builder()
      .uuidRepresentation(org.bson.UuidRepresentation.STANDARD) // BSON 표준 -> 기본 설정
      .applyConnectionString(ConnectionString(uri))
      .readPreference(ReadPreference.primary()) // 모든 읽기 작업이 primary 노드에서 수행
      .build()

    for (c in MongoTableCollector.entries) {
      try {
        val client = MongoClients.create(settings)
        mapper[c.name] = MongoTemplate(client, c.table)
      } catch (e: Exception) {
        throw CustomException(ErrorCode.FAILED_TO_CONNECT_MONGO, e.message)
      }
    }

    return mapper
  }
}