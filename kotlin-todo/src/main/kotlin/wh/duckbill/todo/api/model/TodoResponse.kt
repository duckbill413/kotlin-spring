package wh.duckbill.todo.api.model

import org.example.todo.domain.Todo
import java.time.LocalDateTime

data class TodoResponse(
    var id: Long,
    var title: String,
    var description: String,
    var done: Boolean,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
) {
    companion object {
        fun of(todo: Todo?): TodoResponse {
            checkNotNull(todo) { "Todo is null" }
            checkNotNull(todo.id) { "Todo Id is null" }

            return TodoResponse(
                id = todo.id,
                title = todo.title,
                description = todo.description,
                done = todo.done,
                createdAt = todo.createdAt,
                updatedAt = todo.updatedAt
            )
        }
    }
}
