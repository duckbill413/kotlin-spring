package org.coroutine.Flow.BASE_5

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
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
}