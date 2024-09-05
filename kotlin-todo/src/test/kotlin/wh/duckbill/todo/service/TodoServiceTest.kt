package wh.duckbill.todo.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit.jupiter.SpringExtension
import wh.duckbill.todo.domain.Todo
import wh.duckbill.todo.domain.TodoRepository
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
class TodoServiceTest {
    @MockkBean
    lateinit var repository: TodoRepository
    lateinit var service: TodoService

    // stub 사용 시점에 초기화
    val stub: Todo by lazy {
        Todo(
            id = 1,
            title = "테스트",
            description = "테스트 상세",
            done = false,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )
    }

    @BeforeEach
    fun setUp() {
        service = TodoService(repository)
    }

    @Test
    fun `한개의 TODO를 반환해야한다`() {
        // given
        every { repository.findByIdOrNull(1) } returns stub

        // when
        val actual = service.findById(1L)

        // then
        assertThat(actual).isNotNull
        assertThat(actual).isEqualTo(stub)
    }
}