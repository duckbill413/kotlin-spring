package wh.duckbill.bank.common.message

import org.slf4j.Logger
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import wh.duckbill.bank.common.exception.CustomException
import wh.duckbill.bank.common.exception.ErrorCode
import wh.duckbill.bank.common.logging.Logging
import java.time.LocalDateTime

enum class Topics(
  val topic: String,
) {
  Transactions("transactions"),
}

@Component
class KafkaProducer(
  private val kafkaTemplate: KafkaTemplate<String, Any>,
  private val log: Logger = Logging.getLogger(KafkaProducer::class.java)
) {
  fun sendMessage(topic: String, message: Any) = Logging.logFor(log) {
    it["topic"] = topic
    it["message"] = message

    val future = kafkaTemplate.send(topic, message)
    future.whenComplete { result, ex ->
      if (ex == null) {
        // 메시지 전송 성공
        log.info("메시지 발행 성공 - topic: $topic - time: ${LocalDateTime.now()}")
      } else {
        log.error("메시지 전송 실패 - ${ex.message}")
        throw CustomException(ErrorCode.FAILED_TO_SEND_MESSAGE)
      }
    }
  }
}