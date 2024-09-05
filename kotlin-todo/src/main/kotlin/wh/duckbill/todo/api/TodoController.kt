package wh.duckbill.todo.api

import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.noContent
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import wh.duckbill.todo.api.model.TodoListResponse
import wh.duckbill.todo.api.model.TodoRequest
import wh.duckbill.todo.api.model.TodoResponse
import wh.duckbill.todo.service.TodoService

@RestController
@RequestMapping("/api/todos")
class TodoController(
    private val todoService: TodoService, // TodoService 의 생성자 주입
) {
    @GetMapping
    fun getAll() =
        ok(TodoListResponse.of(todoService.findAll()))

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) =
        ok(TodoResponse.of(todoService.findById(id)))

    @PostMapping
    fun create(@RequestBody todoRequest: TodoRequest) =
        ok(TodoResponse.of(todoService.create(todoRequest)))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody todoRequest: TodoRequest) =
        ok(TodoResponse.of(todoService.update(id, todoRequest)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        todoService.delete(id)
        return noContent().build()
    }
}