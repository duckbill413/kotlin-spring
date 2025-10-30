package wh.duckbill.kafka.`interface`

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.support.Acknowledgment

interface Handler {
  fun handle(record: ConsumerRecord<String, Any>, acknowledgment: Acknowledgment)
}