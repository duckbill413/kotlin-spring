package org.coroutine.Flow.BASE_5

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap

fun generateMessageId(): String = "msg-${(1..8).map { ('a'..'z') + ('0'..'9') }.map { it.random() }.joinToString("")}}"

data class ChatMessage(
  val id: String = generateMessageId(),
  val sender: String,
  val content: String,
  val roomId: String,
  val timestamp: LocalDateTime = LocalDateTime.now(),
  val type: Message = Message.TEXT,
)

enum class Message {
  TEXT, JOIN, LEAVE, SYSTEM
}

data class UserStatus(
  val userId: String,
  val username: String,
  val isOnline: Boolean,
  val lastSeen: LocalDateTime = LocalDateTime.now(),
  val currentRoomId: String? = null,
)

data class ChatRoomStatus(
  val roomId: String,
  val name: String,
  val participants: Set<String>, // 사용자 ID
  val messageCount: Int = 0,
)


/**
 * 채팅 서버에서 발생하는 모든 이벤트들을 나타내는 sealed class
 * 이벤트들은 채팅 시스템의 상태 변화를 추적하고 모니터링하는데 사용됨
 *
 */
sealed class ServerEvent {
  /**
   * 새로운 채팅 메시지가 수신되었을 때 발생하는 이벤트
   * @property message 수신된 채팅 메시지 정보
   */
  data class MessageReceived(val message: ChatMessage) : ServerEvent()

  /**
   * 사용자가 채팅방에 입장했을 때 발생하는 이벤트
   * @property userId 입장한 사용자의 고유 ID
   * @property username 입장한 사용자의 이름
   * @property roomId 입장한 채팅방의 ID
   */
  data class UserJoined(val userId: String, val username: String, val roomId: String) : ServerEvent()

  /**
   * 사용자가 채팅방을 나갔을 때 발생하는 이벤트
   * @property userId 퇴장한 사용자의 고유 ID
   * @property username 퇴장한 사용자의 이름
   * @property roomId 퇴장한 채팅방의 ID
   */
  data class UserLeft(val userId: String, val username: String, val roomId: String) : ServerEvent()

  /**
   * 새로운 채팅방이 생성되었을 때 발생하는 이벤트
   * @property roomId 생성된 채팅방의 ID
   * @property name 생성된 채팅방의 이름
   */
  data class RoomCreated(val roomId: String, val name: String) : ServerEvent()
}

class ChatService {
  /**
   * MutableSharedFlow
   * - 초기값 불필요
   * - 모든 값을 발행 (중복 허용)
   * 캐시 크기 설정 가능 (replay)
   * 버퍼 크기 설정 가능
   * `emit()` 으로 값 발행
   */
  // 각 채팅방별로 메시지 스트림 (roomId -> SharedFlow<ChatMessage>)
  private val roomMessages = ConcurrentHashMap<String, MutableSharedFlow<ChatMessage>>()

  /**
   * `private` 로 선언하여 클래스 내부에서만 접근 가능
   * `MutableSharedFlow` 로 선언되어 값을 emit 할 수 있음
   */
  private val _serverEvents = MutableSharedFlow<ServerEvent>(
    replay = 100, // 새로운 수신자에게 재생할 이전 값의 수
    extraBufferCapacity = 1000, // 추가 버퍼 용량
  )

  // 값을 변경할 수는 없고 관찰만 가능
  val serverEvents: SharedFlow<ServerEvent> = _serverEvents

  // 채팅방 상태 관리 스트림 (roomId -> StateFlow<ChatRoomStatus>)
  private val roomStatus = ConcurrentHashMap<String, MutableStateFlow<ChatRoomStatus>>()

  /**
   * MutableStateFlow
   * - 반드시 초가값 필요
   * - 항상 값을 하나 유지
   * - 동일한 값은 발행하지 않음 (중복 방지)
   * - 마지막 값만 캐시 (replay=1)
   * - value 프로퍼티로 접근 가능
   */
  // 사용자 상태 관리 (userId -> StateFlow<UserStatus>)
  private val userStatus = ConcurrentHashMap<String, MutableStateFlow<UserStatus>>()

  private val _activeUserCount = MutableStateFlow(0)
  val activeUserCount: StateFlow<Int> = _activeUserCount

  init {
    createRoom(roomId = "general", name = "General Chat Room")
    createRoom(roomId = "tech", name = "Tech Chat Room")
  }

  fun createRoom(roomId: String, name: String) {
    if (roomStatus.containsKey(roomId)) {
      return
    }

    roomMessages[roomId] = MutableSharedFlow(replay = 50, extraBufferCapacity = 100)
    roomStatus[roomId] = MutableStateFlow(ChatRoomStatus(roomId, name, emptySet()))

    // Dispatchers.Default 를 사용하여 CPU 집약적인 작업에 최적화된 스레드 풀에서 실행
    // - 채팅방 생성 이벤트 발행
    // - 시스템 메시지 생성 및 발행
    // - 비동기로 처리되어 메인 스레드 차단하지 않음
    CoroutineScope(Dispatchers.Default).launch {
      _serverEvents.emit(ServerEvent.RoomCreated(roomId, name))
      val systemMessage = ChatMessage(
        sender = "System",
        content = "채팅방 생성",
        roomId = roomId,
        type = Message.SYSTEM,
      )
      roomMessages[roomId]?.emit(systemMessage)
    }
  }

  fun userConnect(userId: String, username: String): Flow<UserStatus> {
    val userState = userStatus.getOrPut(userId) {
      MutableStateFlow(UserStatus(userId, username, isOnline = true))
    }

    CoroutineScope(Dispatchers.Default).launch {
      userState.update { it.copy(isOnline = true, lastSeen = LocalDateTime.now()) }
      _activeUserCount.update { it + 1 }
    }

    return userState
  }

  fun userDisconnect(userId: String) {
    val userStatus = userStatus[userId] ?: return

    CoroutineScope(Dispatchers.Default).launch {
      userStatus.value.currentRoomId?.let { roomId ->
        leaveRoom(userId, userStatus.value.username, roomId)
      }
      userStatus.update { it.copy(currentRoomId = null, lastSeen = LocalDateTime.now(), isOnline = false) }
      _activeUserCount.update { it - 1 }
    }
  }

  fun leaveRoom(userId: String, username: String, roomId: String) {
    val roomState = roomStatus[roomId] ?: return
    val messageFlow = roomMessages[roomId] ?: return

    CoroutineScope(Dispatchers.Default).launch {
      roomState.update {
        it.copy(
          participants = it.participants - userId,
        )
      }
      val leaveMessage = ChatMessage(
        sender = username,
        content = "$username 님이 퇴장하셨습니다.",
        roomId = roomId,
        type = Message.LEAVE,
      )
      messageFlow.emit(leaveMessage)
      _serverEvents.emit(ServerEvent.UserLeft(userId, username, roomId))
    }
  }

  fun joinRoom(userId: String, username: String, roomId: String): Flow<ChatMessage>? {
    val roomState = roomStatus[roomId] ?: return null
    val messageFlow = roomMessages[roomId] ?: return null
    val userStatus = userStatus[userId] ?: return null

    CoroutineScope(Dispatchers.Default).launch {
      userStatus.value.currentRoomId?.let {
        if (it != roomId) {
          leaveRoom(userId, username, it)
        } else {
          return@launch
        }
      }
      userStatus.update { it.copy(currentRoomId = roomId) }
      roomState.update { it.copy(participants = it.participants + userId) }
      val joinMessage = ChatMessage(
        sender = username,
        content = "$username 님이 입장하셨습니다.",
        roomId = roomId,
        type = Message.JOIN,
      )
      messageFlow.emit(joinMessage)
      _serverEvents.emit(ServerEvent.UserJoined(userId, username, roomId))
    }

    return messageFlow
  }

  suspend fun sendMessage(message: ChatMessage) {
    val messageFlow = roomMessages[message.roomId] ?: return
    val roomState = roomStatus[message.roomId] ?: return

    roomState.update { it.copy(messageCount = it.messageCount + 1) }
    messageFlow.emit(message)
    _serverEvents.emit(ServerEvent.MessageReceived(message))
  }

  fun getRoomState(roomId: String): StateFlow<ChatRoomStatus>? = roomStatus[roomId]
  fun getAllRooms(): Flow<List<ChatRoomStatus>> = flow {
    while (true) {
      val rooms = roomStatus.values.map { it.value }
      emit(rooms)
      delay(1000L)
    }
  }
}

class ChatClient(
  private val service: ChatService,
  val userId: String,
  val username: String,
) {
  private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
  private val currentRoomId: String? = null
  private var messageCollectJob: Job? = null

  suspend fun start() {
    val userStatus = service.userConnect(
      userId = userId,
      username = username,
    )

    CoroutineScope(Dispatchers.Default).launch {
      userStatus.collect { status ->
        println("상태 업데이트 $username (${if (status.isOnline) "online " else "offline"})")
      }
    }
  }

  suspend fun joinRoom(roomId: String) {
    messageCollectJob?.cancel()
    println("$username 님이 $roomId 방에 입장하였습니다.")
    val messageFlow = service.joinRoom(userId, username, roomId)
    if (messageFlow == null) {
      println("방을 찾을 수 없습니다. $roomId")
      return
    }
    messageCollectJob = CoroutineScope(Dispatchers.Default).launch {
      messageFlow.collect { message ->
        val time = message.timestamp.format(formatter)
        val sender = if (message.type == Message.SYSTEM) "" else message.sender
        println("[$time] $sender: ${message.content}")
      }
    }

    service.getRoomState(roomId)?.let { roomState ->
      CoroutineScope(Dispatchers.Default).launch {
        roomState.collect { state ->
          println("(${state.participants.size}) 참가 중")
        }
      }
    }
  }

  suspend fun sendMessage(content: String) {
    val roomId = currentRoomId
    if (roomId == null) {
      println("채팅방에 먼저 입장 필수!")
      return
    }

    val message = ChatMessage(
      sender = username,
      content = content,
      roomId = roomId,
    )
    service.sendMessage(message)
  }

  fun disconnect() {
    println("$username 연결 종료")
    service.userDisconnect(userId)
    messageCollectJob?.cancel()
  }
}

fun main() = runBlocking {
  val chatService = ChatService()

  launch {
    println("서버 이벤트 모니터링 시작")
    chatService.serverEvents.collect { event ->
      when (event) {
        is ServerEvent.MessageReceived -> println("메시지 수신 이벤트")
        is ServerEvent.RoomCreated -> println("방 생성 이벤트")
        is ServerEvent.UserJoined -> println("유저 참여 이벤트")
        is ServerEvent.UserLeft -> println("유저 나감 이벤트")
      }
    }
  }

  launch {
    chatService.activeUserCount.collect { count ->
      println("활성 사용자 수 $count")
    }
  }

  // 클라이언트 생성
  val alice = ChatClient(chatService, "user-1", "Alice")
  val bob = ChatClient(chatService, "user-2", "Bob")
  val charlie = ChatClient(chatService, "user-3", "Charlie")

  // 클라이언트 시작
  alice.start()
  bob.start()
  charlie.start()

  // Alice와 Bob은 general 방에 입장
  alice.joinRoom("general")
  delay(500)
  bob.joinRoom("general")
  delay(500)

  // 메시지 교환
  alice.sendMessage("안녕하세요! 모두 잘 지내시나요?")
  delay(800)
  bob.sendMessage("네, 잘 지내요. 오늘 좋은 하루 보내세요!")
  delay(1000)

  // Charlie는 tech 방에 입장
  charlie.joinRoom("tech")
  delay(500)
  charlie.sendMessage("기술 토론방에 오신 것을 환영합니다!")
  delay(800)

  // Bob이 tech 방으로 이동
  bob.joinRoom("tech")
  delay(500)
  bob.sendMessage("안녕하세요 Charlie, 여기서 뵙네요!")
  delay(800)
  charlie.sendMessage("안녕하세요 Bob, 어서오세요!")
  delay(1000)

  // Alice가 tech 방으로 이동
  alice.joinRoom("tech")
  delay(500)
  alice.sendMessage("저도 기술 토론에 참여할게요!")
  delay(1500)

  // 새 채팅방 생성 및 이동
  chatService.createRoom("random", "Random Chat")
  delay(500)
  bob.joinRoom("random")
  delay(500)
  bob.sendMessage("새로운 방이네요! 누가 있나요?")
  delay(1000)

  // 사용자 연결 종료
  bob.disconnect()
  delay(800)
  charlie.disconnect()
  delay(800)
  alice.disconnect()
}