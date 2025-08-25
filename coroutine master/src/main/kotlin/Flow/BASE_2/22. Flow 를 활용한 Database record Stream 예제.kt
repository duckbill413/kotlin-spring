package org.coroutine.Flow.BASE_2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import kotlin.system.measureTimeMillis

data class User(
  val id: Long,
  val username: String,
  val email: String,
  val createdAt: LocalDateTime = LocalDateTime.now()
)

data class UserDTO(
  val id: Long,
  val displayName: String,
  val email: String,
) {

  companion object {
    fun fromUser(user: User) = UserDTO(
      id = user.id,
      displayName = user.username,
      email = user.email,
    )
  }
}

class UserRepository {
  private val users = (1..1000).map {
    User(it.toLong(), "user$it", "user$it@example.com")
  }

  suspend fun getUserByPage(pageSize: Int = 100): Flow<List<User>> = flow {
    val totalPage = (users.size + pageSize - 1) / pageSize
    for (page in 0 until totalPage) {
      delay(100)
      val startIdx = page * pageSize
      val endIdx = minOf(startIdx + pageSize, users.size)
      // TODO: 해당 부분에는 실제 DB 조회하여 값을 emit
      emit(users.subList(startIdx, endIdx))
      println("페이지 $page 로드됨 (${endIdx - startIdx})개 레코드")
    }
  }

  suspend fun findById(id: Long): User? {
    delay(50)
    return users.find { it.id == id }
  }
}

class UserService(private val userRepository: UserRepository) {
  suspend fun getAllUsersAsDTO(): Flow<UserDTO> = userRepository.getUserByPage()
    .onEach { println("페이징 처리하며 가져오는 중") }
    .flatMapConcat { it.asFlow() }
    .map {
      delay(5)
      UserDTO.fromUser(it)
    }
    .flowOn(Dispatchers.Default)


}

fun main(): Unit = runBlocking {
  val repository = UserRepository()
  val service = UserService(repository)

  println("전체 유저 DTO 변환 시작")
  var count = 0
  var time = measureTimeMillis {
    // buffer: Flow 백프레셔 기법을 위해 많이 사용됨
    // 백프레셔는 생성자와 소비자 간의 버퍼 공간을 의미
    // 동기식 문제를 해결하는데 많이 사용됨
    // Flow 의 생산자는 소비자가 데이터를 받을때까지 기다린다. 따라서, buffer 를 둠으로서
    // 소비자가 값을 소비하지 않아도 buffer에 값을 넣어주게 된다. (동기식 -> 비동기식으로 처리)
    service.getAllUsersAsDTO().buffer(50).collect { it ->
      count++
      if (count % 100 == 0) {
        println("$count 사용자 처리 완료")
        println("$it")
      }
      delay(2)
    }
  }

  print("\n총 유저 처리 소요 시간 ${time}ms\n\n\n")

  // 유저 ID 조회 시나리오
  val userId = 42L
  println("$userId 조회 요청")
  flow {
    val user = repository.findById(userId)
    if (user != null) {
      emit(user)
    } else {
      throw NoSuchElementException("User $userId not found")
    }
  }.map { UserDTO.fromUser(it) }
    .catch { e -> emit(UserDTO(-1, "unknown", "unknown")) }
    .collect { println("$it 찾음") }
}