package org.coroutine.Flow.BASE_3

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import kotlin.random.Random

/**
 * 데이터 모델 계층
 * - 단순하게 임시로 데이터를 생성
 * 로그 생성 계층
 * - 로그를 예시로 생성한느 역할을 수행
 * 분석 계층
 * - 들어오는 로그를 분석
 * main
 * - 실행 계층
 */

/**
 * 다양한 로깅 레벨을 나타냅니다
 * 로그 메시지를 심각도/중요도에 따라 분류하는데 사용됩니다
 */
enum class LogLevel(val message: String) {
  INFO("INFO"),    // Information messages
  DEBUG("DEBUG"),  // Debug level messages
  WARN("WARN"),    // Warning messages
  ERROR("ERROR")   // Error messages
}

/**
 * 단일 로그 항목을 나타내는 데이터 클래스
 * @property timestamp 로그가 생성된 시간
 * @property level 로그의 심각도 레벨
 * @property service 로그를 생성한 서비스의 이름
 * @property message 실제 로그 메시지
 * @property metadata 추가 정보를 위한 키-값 쌍
 */
data class LogEntry(
  val timestamp: LocalDateTime,
  val level: LogLevel,
  val service: String,
  val message: String,
  val metadata: Map<String, String> = emptyMap()
)


class LogStreamGenerator {
  private val service =
    listOf("auth-service", "user-service", "product-service", "payment-service", "notification-service")
  private val debugMessages =
    listOf("Processing user login attempt", "Checking cart status", "Initiating payment process")
  private val infoMessages = listOf("User account created", "Product added to cart", "Payment completed successfully")
  private val warningMessages =
    listOf("Multiple login attempts detected", "Product stock running low", "Payment processing delayed")
  private val errorMessages =
    listOf("Failed to authenticate user", "Database connection error", "Payment transaction failed")

  /**
   * Kafka 나 RabbitMQ 를 통해 유입 가정
   */
  fun generateLogStream(): Flow<LogEntry> = flow {
    repeat(500) {
      delay(10L)
      emit(createRandomLogEntry())
    }
  }

  private fun createRandomLogEntry(): LogEntry {
    val level = LogLevel.values()[Random.nextInt(LogLevel.values().size)]
    val service = service.random()
    val message = when (level) {
      LogLevel.INFO -> infoMessages.random()
      LogLevel.DEBUG -> debugMessages.random()
      LogLevel.WARN -> warningMessages.random()
      LogLevel.ERROR -> errorMessages.random()
    }
    return LogEntry(
      timestamp = LocalDateTime.now(),
      level = level,
      service = service,
      message = message
    )
  }
}

/**
 * 로그 항목을 분석하기 위한 서비스 클래스
 */
class LogAnalysisService {

  /**
   * 로그 항목을 읽기 쉬운 문자열 형식으로 변환합니다
   * @param logFlow 로그 항목의 입력 플로우
   * @return 형식화된 로그 문자열의 플로우
   */
  fun formatLogEntries(logFlow: Flow<LogEntry>): Flow<String> = logFlow.map { log ->
    "${log.timestamp} [${log.level.name}] ${log.service} - ${log.message}"
  }

  /**
   * 입력 플로우에서 ERROR 레벨의 로그만 필터링합니다
   * @param logFlow 로그 항목의 입력 플로우
   * @return 에러 로그만 포함된 플로우
   */
  fun filterErrorLogs(logFlow: Flow<LogEntry>): Flow<LogEntry> = logFlow.filter { it.level == LogLevel.ERROR }

  /**
   * 서비스별 로그 수를 계산합니다
   * @param logFlow 로그 항목의 입력 플로우
   * @return 서비스 이름과 해당 로그 수의 맵
   */
  suspend fun countLogsByService(logFlow: Flow<LogEntry>): Map<String, Int> = logFlow
    .fold(mutableMapOf()) { count, log ->
      count[log.service] = (count[log.service] ?: 0) + 1
      count
    }
}

/**
 * 로그 분석 기능을 시연하는 메인 함수
 * - 로그 생성기와 분석 서비스를 생성
 * - 여러 수집기 간에 로그 스트림을 공유
 * - 형식화된 로그와 에러 로그를 출력
 * - 서비스별 로그 수를 표시
 */
fun main(): Unit = runBlocking {
  val logStreamGenerator = LogStreamGenerator()
  val logAnalysisService = LogAnalysisService()

  val logStream = logStreamGenerator.generateLogStream().shareIn(
    scope = CoroutineScope(Dispatchers.IO),
    started = SharingStarted.Eagerly,
    replay = 100
  )

  logAnalysisService.formatLogEntries(logStream).take(10).collect { println(it) }
  println()
  logAnalysisService.filterErrorLogs(logStream).take(10).collect { println(it) }
  println()

  val logList = logStreamGenerator.generateLogStream().toList()
  val serviceCount = logList.groupBy { it.service }.mapValues { it.value.size }
  serviceCount.forEach { (service, count) -> println("$service : $count") }
}