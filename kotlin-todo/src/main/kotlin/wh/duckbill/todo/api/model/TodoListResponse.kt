package wh.duckbill.todo.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import wh.duckbill.todo.domain.Todo

data class TodoListResponse(
    val items: List<TodoResponse>,
) {
    val size: Int
        @JsonIgnore
        get() = items.size

    fun get(index: Int) = items[index]

    // java의 static method와 유사한 기능
    companion object {
        fun of(todoList: List<Todo>) =
            TodoListResponse(todoList.map(TodoResponse::of))
    }
}
