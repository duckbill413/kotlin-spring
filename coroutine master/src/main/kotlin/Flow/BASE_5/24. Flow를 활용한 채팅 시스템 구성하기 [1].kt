package org.coroutine.Flow.BASE_5

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
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

sealed class ServerEvent {
  data class MessageReceived(val message: ChatMessage) : ServerEvent()
  data class UserJoined(val userId: String, val username: String, val roomId: String) : ServerEvent()
  data class UserLeft(val userId: String, val username: String, val roomId: String) : ServerEvent()
  data class RoomCreated(val roomId: String, val name: String) : ServerEvent()
}

class ChatService {
  // 각 채팅방별로 메시지 스트림 (roomId -> SharedFlow<ChatMessage>)
  private val roomMessages = ConcurrentHashMap<String, MutableSharedFlow<ChatMessage>>()

  private val _serverEvents = MutableSharedFlow<ServerEvent>(
    replay = 100,
    extraBufferCapacity = 1000,
  )
  val serverEvents: SharedFlow<ServerEvent> = _serverEvents

  // 채팅방 상태 관리 스트림 (roomId -> StateFlow<ChatRoomStatus>)
  private val roomStatus = ConcurrentHashMap<String, MutableStateFlow<ChatRoomStatus>>()

  // 사용자 상태 관리 (userId -> StateFlow<UserStatus>)
  private val userStatus = ConcurrentHashMap<String, MutableStateFlow<UserStatus>>()

  private val _activeUserCount = MutableStateFlow(0)
  val activeUserCount: StateFlow<Int> = _activeUserCount

  init {
    createRoom(roomId = "general", name = "General Chat Room")
    createRoom(roomId = "sales", name = "Sales Chat Room")
  }

  fun createRoom(roomId: String, name: String) {
    if (roomStatus.containsKey(roomId)) {
      return
    }

    roomMessages[roomId] = MutableSharedFlow(replay = 50, extraBufferCapacity = 100)
    roomStatus[roomId] = MutableStateFlow(ChatRoomStatus(roomId, name, emptySet()))

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