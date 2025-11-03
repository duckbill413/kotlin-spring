package wh.duckbill.kafka.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.AcknowledgingMessageListener
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.serializer.JsonDeserializer
import wh.duckbill.kafka.common.exception.CustomException
import wh.duckbill.kafka.common.exception.ErrorCode
import wh.duckbill.kafka.`interface`.Handler

@EnableKafka
@Configuration
class KafkaConsumerConfig(
  private val topicConfig: TopicConfig,
  private val logger: Logger = LoggerFactory.getLogger(KafkaConsumerConfig::class.java),
) {

  @Bean
  fun consumerConfig(): Map<String, Any> {
    val props: MutableMap<String, Any> = HashMap()

    // 고정값
    props[ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG] = 1000L // 연결이 실패하는 경우에 대해서 재연결 시도 간격 (ms)
    props[ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG] = 10000L  // 최대 재연결 간격 (ms)
    props[ConsumerConfig.RETRY_BACKOFF_MS_CONFIG] = 5000L // 실패한 요청에 대해서 재시도 간격 (ms)

    props[ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG] = 15000L // 컨슈머와 브로커와의 세션을 유지하는 최대 시간
    props[ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG] = 3000L  // heartbeat 전송 간격

    // config value
    props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = topicConfig.info.bootstrapServers // 연결하고자 하는 서버 url
    /**
     * 총 100개의 메시지가 있는 경우
     * earliest: 새로운 컨슈머가 값을 읽기 시작 -> 0 부터 읽기 시작 0...99
     * lastest: 마지막 99번째의 데이터만 읽음
     */
    props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] =
      topicConfig.info.consumer.autoOffsetReset // 초기의 오프셋 위치를 설정 (earliest, latest, none)
    props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] =
      topicConfig.info.consumer.autoCommit // auto commit (메시지를 읽은 후 명시적으로 커밋을 설정 <= false)
    props[ConsumerConfig.GROUP_ID_CONFIG] = topicConfig.info.consumer.groupId

    props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
    props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
    return props
  }

  @Bean(name = ["factoryHandlerMapper"])
  fun factoryHandlerMapper(): Map<String, ConcurrentKafkaListenerContainerFactory<String, Any>> {
    val factoryMap = mutableMapOf<String, ConcurrentKafkaListenerContainerFactory<String, Any>>()
    topicConfig.topics.forEach { (topicName, properties) ->
      if (properties.enabled) {
        var handler: Handler
        when (topicName) {
          // 필요한 경우에 따라 핸들러 매핑
          // "transactions" -> handler = TransactionHandler()
          TODO("핸들러 매핑")
          else -> {
            throw CustomException(ErrorCode.FAILED_TO_FIND_TOPIC)
          }
        }

        factoryMap[topicName] = createKafkaListenerContainerFactory(topicName, handler, properties)
      }
    }

    return factoryMap
  }


  private fun createKafkaListenerContainerFactory(
    topicName: String,
    handler: Handler,
    properties: TopicProperties,
  ): ConcurrentKafkaListenerContainerFactory<String, Any> {
    val config = consumerConfig().toMutableMap()

    config[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = properties.maxPollRecords  // 단일 폴링 레코드에서 최대 반환 가능한 레코드의 개수

    val consumerFactory = DefaultKafkaConsumerFactory<String, Any>(config)
    val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()

    factory.consumerFactory = consumerFactory
    factory.containerProperties.ackMode =
      ContainerProperties.AckMode.MANUAL  // 데이터 일관성 보장에 대한 옵션 (메시지 처리후 명시적 처리 autoCommit -> false 인 경우 주로 사용
    factory.setAutoStartup(true)  // 애플리케이션 시작 시점에 자동으로 컨테이너를 시작
    factory.containerProperties.pollTimeout = properties.pollingInterval

    val container = factory.createContainer(topicName)

    // 메시지 처리 및 수동 확인을 지원하는 리스너
    container.setupMessageListener(AcknowledgingMessageListener { record, acknowledgment ->
      try {
        handler.handle(record, acknowledgment)
      } catch (e: Exception) {
        handler.handleDLQ(record, acknowledgment)
      }
    })

    return factory
  }
}