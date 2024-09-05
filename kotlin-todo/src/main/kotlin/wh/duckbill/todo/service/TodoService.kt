package wh.duckbill.todo.service

import org.example.todo.domain.Todo
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import wh.duckbill.todo.api.model.TodoRequest
import wh.duckbill.todo.domain.TodoRepository
import java.time.LocalDateTime

@Service
class TodoService(
    private val todoRepository: TodoRepository,
) {
    @Transactional(readOnly = true)
    fun findAll(): List<Todo> =
        todoRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))

    @Transactional(readOnly = true)
    fun findById(id: Long): Todo =
        todoRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @Transactional
    fun create(todoRequest: TodoRequest?): Todo {
        checkNotNull(todoRequest) { "TodoRequest is null" }
        val todo = Todo(
            title = todoRequest.title,
            description = todoRequest.description,
            done = todoRequest.done,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        return todoRepository.save(todo)
    }

    @Transactional
    fun update(id: Long, todoRequest: TodoRequest?): Todo {
        checkNotNull(todoRequest) { "TodoRequest is null" }

        return this.findById(id).let {
            it.update(todoRequest.title, todoRequest.description, todoRequest.done)
            todoRepository.save(it)
        }
    }


    fun delete(id: Long) =
        todoRepository.deleteById(id)
}