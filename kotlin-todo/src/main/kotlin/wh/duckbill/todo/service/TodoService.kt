package wh.duckbill.todo.service

import com.sun.tools.javac.jvm.ByteCodes.ret
import org.example.todo.domain.Todo
import org.springframework.stereotype.Service
import wh.duckbill.todo.api.model.TodoRequest

@Service
class TodoService {
    fun findAll(): List<Todo> {
        TODO("Not yet implemented")
        return mutableListOf();
    }

    fun findById(id: Long): Any {
        TODO("Not yet implemented")
    }

    fun create(todoRequest: TodoRequest): Any {
        TODO("Not yet implemented")
    }

    fun update(todoRequest: TodoRequest): Any {
        TODO("Not yet implemented")
    }

    fun delete(id: Long) {
        TODO("Not yet implemented")
    }
}