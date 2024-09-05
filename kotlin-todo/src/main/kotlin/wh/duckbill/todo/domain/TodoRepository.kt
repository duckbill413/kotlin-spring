package wh.duckbill.todo.domain

import org.example.todo.domain.Todo
import org.springframework.data.jpa.repository.JpaRepository

interface TodoRepository : JpaRepository<Todo, Long> {
    fun findAllByDoneIsFalseOrderByIdDesc(): List<Todo>?
}